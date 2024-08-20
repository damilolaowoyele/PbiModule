package org.fhi360.PbiModule.Repository;

import org.fhi360.PbiModule.Model.PbiEmployeeInactivity.PbiEmployeeInactivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PbiEmployeeInactivityRepository extends JpaRepository<PbiEmployeeInactivity, UUID> {
    List<PbiEmployeeInactivity> findByEmployeeId(UUID employeeId);
}
