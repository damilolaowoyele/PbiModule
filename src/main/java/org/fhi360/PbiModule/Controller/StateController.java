package org.fhi360.PbiModule.Controller;


import lombok.RequiredArgsConstructor;
import org.fhi360.PbiModule.Model.StatesLgasFacilities.State;
import org.fhi360.PbiModule.Service.StateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("/api/states")
@RestController
public class StateController {
    private final StateService stateService;

    @GetMapping
    public ResponseEntity<?> getAllStates() {
        List<State> states = stateService.getAllStates();
        try {
            return ResponseEntity.ok(states);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

