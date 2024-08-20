package org.fhi360.PbiModule.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.PbiModule.Exception.ResourceNotFoundException;
import org.fhi360.PbiModule.Model.PbiEmployee.PbiEmployee;
import org.fhi360.PbiModule.Service.PbiEmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/pbiEmployees")
@RequiredArgsConstructor
public class PbiEmployeeController {

    private final PbiEmployeeService pbiEmployeeService;

    @PostMapping
    public ResponseEntity<?> createPbiEmployee(@Valid @RequestBody PbiEmployee pbiEmployee,
                                               @RequestParam(defaultValue = "false") boolean forceSave) {
        try {
            Map<String, Object> result = pbiEmployeeService.savePbiEmployee(pbiEmployee, forceSave, false);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/import")
    public ResponseEntity<?> importCsvFile(@RequestParam("file") MultipartFile file) {
        try {
            List<PbiEmployee> importedEmployees = pbiEmployeeService.importCsvFile(file);
            return ResponseEntity.ok(importedEmployees);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<PbiEmployee> getPbiEmployeeById(@PathVariable UUID id) {
        PbiEmployee pbiEmployee = pbiEmployeeService.getPbiEmployeeById(id);
        if (pbiEmployee == null) {
            throw new ResourceNotFoundException("Employee not found with id " + id);
        }
        return ResponseEntity.ok().body(pbiEmployee);
    }

    @GetMapping
    public ResponseEntity<Page<PbiEmployee>> getPbiEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String lga,
            @RequestParam(required = false) String facility,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PbiEmployee> pbiEmployees = pbiEmployeeService.getPbiEmployees(pageable, searchText, sortBy, sortDirection, state, lga, facility, status);
        System.out.println("The total pages is " + pbiEmployees.getTotalPages());
        return ResponseEntity.ok().body(pbiEmployees);
    }


    @GetMapping("/all")
    public ResponseEntity<List<PbiEmployee>> getAllPbiEmployees() {
        List<PbiEmployee> pbiEmployees = pbiEmployeeService.getAllPbiEmployees();
        return ResponseEntity.ok().body(pbiEmployees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePbiEmployee(@PathVariable UUID id, @Valid @RequestBody PbiEmployee pbiEmployee) {
        Map<String, Object> result = pbiEmployeeService.updatePbiEmployee(id, pbiEmployee);
        if (result.containsKey("errors")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> partialUpdatePbiEmployee(@PathVariable UUID id, @Valid @RequestBody Map<String, Object> updates) {
        Map<String, Object> result = pbiEmployeeService.partialUpdatePbiEmployee(id, updates);
        if (result.containsKey("errors")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePbiEmployee(@PathVariable UUID id) {
        pbiEmployeeService.deletePbiEmployee(id);
        return ResponseEntity.ok().build();
    }
}
