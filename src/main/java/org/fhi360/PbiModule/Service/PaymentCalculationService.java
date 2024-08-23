package org.fhi360.PbiModule.Service;

import lombok.RequiredArgsConstructor;
import org.fhi360.PbiModule.Model.PaymentSetting;
import org.fhi360.PbiModule.Model.PbiEmployee.PbiEmployee;
import org.fhi360.PbiModule.Model.PbiPayment;
import org.fhi360.PbiModule.Repository.PaymentSettingRepository;
import org.fhi360.PbiModule.Repository.PbiEmployeeRepository;
import org.fhi360.PbiModule.Repository.PbiPaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCalculationService {
    private final PbiEmployeeRepository pbiEmployeeRepository;
    private final PaymentSettingRepository paymentSettingRepository;
    private final RoutineVaccinationService routineVaccinationService;
    private final PbiPaymentRepository pbiPaymentRepository;
    private final PbiEmployeeInactivityService pbiEmployeeInactivityService;


    @Transactional(readOnly = true)
    public Page<PbiPayment> calculatePayments(LocalDate fromDate, LocalDate toDate, String searchTerm, String state, String lga, String facility, String teamType, Pageable pageable) {

        Specification<PbiEmployee> spec = Specification.where((root, query, cb) ->
                cb.or(
                        cb.notEqual(root.get("status"), "Inactive"),
                        cb.isFalse(root.get("suspectedDuplicate"))
                )
        );

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String lowerCaseSearchTerm = searchTerm.toLowerCase();
            String[] searchWords = lowerCaseSearchTerm.split("\\s+");

            for (String word : searchWords) {
                spec = spec.and((root, query, cb) ->
                        cb.or(
                                cb.like(cb.lower(root.get("firstName")), "%" + word + "%"),
                                cb.like(cb.lower(root.get("middleName")), "%" + word + "%"),
                                cb.like(cb.lower(root.get("lastName")), "%" + word + "%"),
                                cb.like(cb.lower(root.get("accountNumber")), "%" + word + "%"),
                                cb.like(cb.lower(root.get("accountName")), "%" + word + "%"),
                                cb.like(cb.lower(root.get("teamUsername")), "%" + word + "%")
                        )
                );
            }
        }



        if (state != null && !state.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("state"), state));
        }

        if (lga != null && !lga.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("lga"), lga));
        }

        if (facility != null && !facility.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("facility"), facility));
        }

        if (teamType != null && !teamType.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("teamType"), teamType));
        }

        Page<PbiEmployee> employeesPage = pbiEmployeeRepository.findAll(spec, pageable);
        
//        PaymentSetting paymentSetting = paymentSettingRepository.getFixedCost()
//                .orElseThrow(() -> new RuntimeException("Payment settings not found"));

        List<PbiPayment> payments = employeesPage.getContent().stream()
                .map(employee -> {
                    PaymentSetting paymentSetting = getPaymentSettingForEmployee(employee);
                    return calculateEmployeePayment(employee, fromDate, toDate, paymentSetting);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(payments, pageable, employeesPage.getTotalElements());
    }

    private PaymentSetting getPaymentSettingForEmployee(PbiEmployee employee) {
        return paymentSettingRepository.findByTeamType(employee.getTeamType().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Payment settings not found for team type: " + employee.getTeamType()));
    }


    private PbiPayment calculateEmployeePayment(PbiEmployee employee, LocalDate fromDate, LocalDate toDate, PaymentSetting paymentSetting) {
        // ... calculation logic ...
        long daysSelected = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
//      long daysWorked = routineVaccinationService.countDistinctDatesByEmployeeAndDateRange(employee.getTeamUsername(), fromDate, toDate);
        long daysWorked = routineVaccinationService.countDistinctDatesByEmployeeAndDateRange(employee.getTeamUsername(), fromDate, toDate, employee.getPeriodsOfInactivity());
        long numOfPersonsVaccinated = routineVaccinationService.countPersonsVaccinatedByEmployeeAndDateRange(employee.getTeamUsername(), fromDate, toDate, employee.getPeriodsOfInactivity());
        int numOfVaccinations = routineVaccinationService.countVaccinationsByEmployeeAndDateRange(employee.getTeamUsername(), fromDate, toDate, employee.getPeriodsOfInactivity());

        BigDecimal transportPerDay = paymentSetting.getDailyTransportCost();
        BigDecimal costPerVaccination = paymentSetting.getCostPerVaccination();

        BigDecimal totalTransport = BigDecimal.valueOf(daysWorked).multiply(transportPerDay);
        BigDecimal totalPbi = BigDecimal.valueOf(numOfVaccinations).multiply(costPerVaccination);
        BigDecimal totalInternetCost = calculateInternetCost(fromDate, toDate, paymentSetting.getWeeklyInternetCost());

        BigDecimal previousTravelAdvance = getPreviousTravelAdvance(employee, fromDate);
        BigDecimal total = totalTransport.add(totalPbi).add(totalInternetCost).subtract(previousTravelAdvance);


        PbiPayment pbiPayment = new PbiPayment();
        pbiPayment.setFirstName(employee.getFirstName());
        pbiPayment.setLastName(employee.getLastName());
        pbiPayment.setState(employee.getState());
        pbiPayment.setLga(employee.getLga());
        pbiPayment.setFacility(employee.getFacility());
        pbiPayment.setTeam(employee.getTeam());
        pbiPayment.setDesignation(employee.getDesignation());
        pbiPayment.setStatus(employee.getStatus());
        pbiPayment.setFromDate(fromDate);
        pbiPayment.setToDate(toDate);
        pbiPayment.setDaysSelected((int) daysSelected);
        pbiPayment.setDaysWorked((int) daysWorked);
        pbiPayment.setTransportPerDay(transportPerDay);
        pbiPayment.setPreviousTravelAdvance(previousTravelAdvance);
        pbiPayment.setTotalTransport(totalTransport);
        pbiPayment.setNumOfPersonsVaccinated((int) numOfPersonsVaccinated);
        pbiPayment.setNumOfVaccinations(numOfVaccinations);
        pbiPayment.setCostPerVaccination(costPerVaccination);
        pbiPayment.setTotalPbi(totalPbi);
        pbiPayment.setTotalInternetCost(totalInternetCost);
        pbiPayment.setTotal(total);
        pbiPayment.setAccountNumber(employee.getAccountNumber());
        pbiPayment.setAccountName(employee.getAccountName());
        pbiPayment.setBank(employee.getBank());

        return pbiPayment;
    }

    @Transactional
    public PbiPayment savePayment(PbiPayment payment) {
        PbiPayment record = new PbiPayment();
        return pbiPaymentRepository.save(record);
    }

    private BigDecimal calculateInternetCost(LocalDate fromDate, LocalDate toDate, BigDecimal weeklyInternetCost) {
        //TODO: Review this method
//        long weeks = ChronoUnit.WEEKS.between(fromDate, toDate);
//        return BigDecimal.valueOf(weeks).multiply(weeklyInternetCost);

        return BigDecimal.valueOf(0);
    }

    private BigDecimal getPreviousTravelAdvance(PbiEmployee employee, LocalDate fromDate) {
        //TODO: implement this method
//        return pbiPaymentRepository.findTopByPbiEmployeeAndToDateBeforeOrderByToDateDesc(employee, fromDate)
//                .map(PbiPayment::getTotalTransport)
//                .orElse(BigDecimal.ZERO);
        return BigDecimal.valueOf(0);
    }

    public String exportPaymentsToCsv(List<PbiPayment> payments, String filePath) throws IOException {
        if (payments == null || filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Payments list and file path cannot be null or empty");
        }

        String[] headers = {"No", "First Name", "Last Name", "State", "LGA", "Facility", "Team", "Designation",
                "Status", "From Date", "To Date", "Days Selected", "Days Worked", "Transport Per Day",
                "Previous Travel Allowance", "Total Transport", "Number of Persons Vaccinated", "Number of Vaccinations",
                "Cost Per Vaccination", "Total PBI", "Total Internet Cost", "Total",
                "Account Number", "Account Name", "Bank"};

        try (FileWriter csvWriter = new FileWriter(filePath)) {
            // Write headers
            csvWriter.append(String.join(",", headers));
            csvWriter.append("\n");


            // Write data
            for (int i = 0; i < payments.size(); i++) {
                csvWriter.append(createCsvLine(i + 1, payments.get(i)));
                csvWriter.append("\n");
            }

            csvWriter.flush();
        } catch (IOException e) {
            // Log error or rethrow if necessary
            throw new IOException("Error writing to CSV file", e);
        }

        return filePath;
    }

    private String createCsvLine(int number, PbiPayment payment) {
        StringBuilder csvLine = new StringBuilder();
        appendCsvField(csvLine, number);
        appendCsvField(csvLine, payment.getFirstName());
        appendCsvField(csvLine, payment.getLastName());
        appendCsvField(csvLine, payment.getState());
        appendCsvField(csvLine, payment.getLga());
        appendCsvField(csvLine, payment.getFacility());
        appendCsvField(csvLine, payment.getTeam());
        appendCsvField(csvLine, payment.getDesignation());
        appendCsvField(csvLine, payment.getStatus());
        appendCsvField(csvLine, payment.getFromDate());
        appendCsvField(csvLine, payment.getToDate());
        appendCsvField(csvLine, payment.getDaysSelected());
        appendCsvField(csvLine, payment.getDaysWorked());
        appendCsvField(csvLine, payment.getTransportPerDay());
        appendCsvField(csvLine, payment.getPreviousTravelAdvance());
        appendCsvField(csvLine, payment.getTotalTransport());
        appendCsvField(csvLine, payment.getNumOfPersonsVaccinated());
        appendCsvField(csvLine, payment.getNumOfVaccinations());
        appendCsvField(csvLine, payment.getCostPerVaccination());
        appendCsvField(csvLine, payment.getTotalPbi());
        appendCsvField(csvLine, payment.getTotalInternetCost());
        appendCsvField(csvLine, payment.getTotal());
        appendCsvField(csvLine, payment.getAccountNumber());
        appendCsvField(csvLine, payment.getAccountName());
        appendCsvField(csvLine, payment.getBank());
        return csvLine.toString();
    }

    private void appendCsvField(StringBuilder csvLine, Object field) {
        if (csvLine.length() > 0) { //or (!csvLine.isNotEmpty()
            csvLine.append(",");
        }
        csvLine.append(field != null ? field.toString() : "");
    }

}