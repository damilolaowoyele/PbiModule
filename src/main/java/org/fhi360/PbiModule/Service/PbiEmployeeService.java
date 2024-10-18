package org.fhi360.PbiModule.Service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.fhi360.PbiModule.Exception.ResourceNotFoundException;
import org.fhi360.PbiModule.Model.PbiEmployee.PbiEmployee;
import org.fhi360.PbiModule.Repository.PbiEmployeeRepository;
import org.fhi360.PbiModule.Service.Util.ValidationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PbiEmployeeService {

    private final PbiEmployeeRepository pbiEmployeeRepository;
    private final Validator validator;
    private static final List<String> SORTABLE_FIELDS = Arrays.asList("state", "status", "designation", "sex", "lastModifiedDate", "createdDate");

    public Map<String, Object> savePbiEmployee(PbiEmployee pbiEmployee, boolean forceSave, boolean isUpdate) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = new ArrayList<>();

        try {
            ValidationUtil.validate(pbiEmployee, validator);

            // Check for exact matches only if it's not an update
            if (!isUpdate) {
                // Check for exact matches
                List<PbiEmployee> exactMatches = pbiEmployeeRepository.findByIdNumberOrPhoneNumberOrAccountNumber(
                        pbiEmployee.getIdNumber(), pbiEmployee.getPhoneNumber(), pbiEmployee.getAccountNumber());
                if (!exactMatches.isEmpty()) {
                    throw new RuntimeException("User already registered: " + exactMatches.get(0).getFirstName() + " " + exactMatches.get(0).getLastName());
                }

                // Check for suspected duplicates
                List<PbiEmployee> probableMatches = findProbableMatches(pbiEmployee);
                if (!probableMatches.isEmpty()) {
                    if (!forceSave) {
                        response.put("suspectedDuplicate", true);
                        return response;
                    } else {
                        // Set new user's status to "Inactive" and suspectedDuplicate to true
                        pbiEmployee.setStatus("Inactive");
                        pbiEmployee.setSuspectedDuplicate(true);
                        // Set probable matches' suspectedDuplicate to true
                        for (PbiEmployee match : probableMatches) {
                            match.setSuspectedDuplicate(true);
                            pbiEmployeeRepository.save(match);
                        }
                    }
                }
            }

            // Save the new user
            PbiEmployee savedEmployee = pbiEmployeeRepository.save(pbiEmployee);
            response.put("employee", savedEmployee);

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

        return response;
    }

    private List<PbiEmployee> findProbableMatches(PbiEmployee newEmployee) {
        return pbiEmployeeRepository.findProbableMatches(
                newEmployee.getFirstName(), newEmployee.getMiddleName(), newEmployee.getLastName(),
                newEmployee.getAccountName(), newEmployee.getHomeAddress(), newEmployee.getEmailAddress());
    }

    public List<PbiEmployee> importCsvFile(MultipartFile file) throws Exception {
        List<String> requiredColumns = Arrays.asList("state", "cluster", "lga", "team", "facility", "teamUsername", "firstName", "middleName", "lastName", "sex", "designation", "newIntake", "previousExperience", "qualification", "phoneNumber", "internetPhoneNumber", "governmentEmployee", "emailAddress", "homeAddress", "accountName", "bank", "accountNumber", "meansOfIdentification", "idNumber", "status");

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new RuntimeException("CSV file is empty");
            }

            Set<String> headerColumns = new HashSet<>(Arrays.asList(headerLine.split(",")));
            for (String requiredColumn : requiredColumns) {
                if (!headerColumns.contains(requiredColumn)) {
                    throw new RuntimeException("Missing required column: " + requiredColumn);
                }
            }

            CsvToBean<PbiEmployee> csvToBean = new CsvToBeanBuilder<PbiEmployee>(reader)
                    .withType(PbiEmployee.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<PbiEmployee> pbiEmployees = csvToBean.parse();
            List<PbiEmployee> validEmployees = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (PbiEmployee employee : pbiEmployees) {
                try {
                    // Remove trailing spaces from all string fields using reflection
                    removeTrailingSpaces(employee);
                    ValidationUtil.validate(employee, validator);
                    validEmployees.add(employee);
                } catch (RuntimeException e) {
                    errors.add("Row " + (pbiEmployees.indexOf(employee) + 1) + ": " + e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                throw new RuntimeException("Validation errors occurred:\n" + String.join("\n", errors));
            }

            return pbiEmployeeRepository.saveAll(validEmployees);
        }
    }

    private void removeTrailingSpaces(PbiEmployee employee) {
        Field[] fields = PbiEmployee.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(employee);
                    if (value != null) {
                        field.set(employee, value.stripTrailing());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
        }
    }


    public Page<PbiEmployee> getPbiEmployees(Pageable pageable, String searchText, String sortBy, String sortDirection,
                                             String state, String lga, String facility, String status) {
        Specification<PbiEmployee> spec = createSearchSpecification(searchText);

        if (state != null && !state.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("state"), state));
        }

        if (lga != null && !lga.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("lga"), lga));
        }

        if (facility != null && !facility.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("facility"), facility));
        }

        if (status != null && !status.isEmpty()) {
            if (status.equals("Duplicates")) {
                spec = spec.and((root, query, cb) -> cb.isTrue(root.get("suspectedDuplicate")));
            } else {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
            }
        }

        // Validate and set the sort field
        String validSortBy = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "createdDate";

        // Create a Sort object
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), validSortBy);

        // Create a new Pageable object that includes the validated sorting
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return pbiEmployeeRepository.findAll(spec, sortedPageable);
    }

    private Specification<PbiEmployee> createSearchSpecification(String searchText) {
        String lowerCaseSearchText = searchText.toLowerCase();
        String[] searchWords = lowerCaseSearchText.split("\\s+");

        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Create predicates for each word to match against all specified properties
            for (String word : searchWords) {
                List<jakarta.persistence.criteria.Predicate> wordPredicates = new ArrayList<>();
                wordPredicates.add(cb.like(cb.lower(root.get("firstName")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("middleName")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("lastName")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("emailAddress")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("phoneNumber")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("state")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("lga")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("status")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("designation")), "%" + word + "%"));
                wordPredicates.add(cb.like(cb.lower(root.get("sex")), "%" + word + "%"));
                predicates.add(cb.or(wordPredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }


    public PbiEmployee getPbiEmployeeById(UUID id) {
        return pbiEmployeeRepository.findById(id).orElse(null);
    }


    public List<PbiEmployee> getAllPbiEmployees() {
        return pbiEmployeeRepository.findAllByOrderByCreatedDateDesc();
    }


    public Map<String, Object> updatePbiEmployee(UUID id, PbiEmployee pbiEmployeeDetails) {
        return pbiEmployeeRepository.findById(id)
                .map(pbiEmployee -> {
                    BeanUtils.copyProperties(pbiEmployeeDetails, pbiEmployee, "id");
                    return savePbiEmployee(pbiEmployee, true, true);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }

    public Map<String, Object> partialUpdatePbiEmployee(UUID id, Map<String, Object> updates) {
        PbiEmployee pbiEmployee = pbiEmployeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        updates.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(PbiEmployee.class, k);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, pbiEmployee, v);
            }
        });

        return savePbiEmployee(pbiEmployee, true, true);
    }

    public void deletePbiEmployee(UUID id) {
        pbiEmployeeRepository.deleteById(id);
    }
}
