package org.fhi360.PbiModule.Controller;

import org.fhi360.PbiModule.Model.PbiEmployeeInactivity.PbiEmployeeInactivity;
import org.fhi360.PbiModule.Service.PbiEmployeeInactivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inactivities")
public class PbiEmployeeInactivityController {

    @Autowired
    private PbiEmployeeInactivityService service;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getInactivitiesByEmployeeId(@PathVariable UUID employeeId) {
        try{
            List<PbiEmployeeInactivity> inactivities = service.getInactivitiesByEmployeeId(employeeId);
            return ResponseEntity.ok().body(inactivities);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addInactivity(@RequestBody PbiEmployeeInactivity inactivity) {
        try {
            PbiEmployeeInactivity savedInactivity = service.addInactivity(inactivity);
            return ResponseEntity.ok().body(savedInactivity);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInactivity(@PathVariable UUID id) {
        service.deleteInactivity(id);
        return ResponseEntity.ok().build();
    }
}
