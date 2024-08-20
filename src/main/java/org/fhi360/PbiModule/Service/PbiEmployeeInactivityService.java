package org.fhi360.PbiModule.Service;

import org.fhi360.PbiModule.Model.PbiEmployeeInactivity.PbiEmployeeInactivity;
import org.fhi360.PbiModule.Repository.PbiEmployeeInactivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PbiEmployeeInactivityService {

    @Autowired
    private PbiEmployeeInactivityRepository repository;

    public List<PbiEmployeeInactivity> getInactivitiesByEmployeeId(UUID employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    public PbiEmployeeInactivity addInactivity(PbiEmployeeInactivity inactivity) {
        return repository.save(inactivity);
    }

    public void deleteInactivity(UUID id) {
        repository.deleteById(id);
    }
}
