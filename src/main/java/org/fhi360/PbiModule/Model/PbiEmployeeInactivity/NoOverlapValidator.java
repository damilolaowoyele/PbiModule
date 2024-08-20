package org.fhi360.PbiModule.Model.PbiEmployeeInactivity;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.fhi360.PbiModule.Repository.PbiEmployeeInactivityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NoOverlapValidator implements ConstraintValidator<NoOverlap, PbiEmployeeInactivity> {

    @Autowired
    private PbiEmployeeInactivityRepository repository;

    @Override
    public boolean isValid(PbiEmployeeInactivity inactivity, ConstraintValidatorContext context) {
        List<PbiEmployeeInactivity> existingInactivities = repository.findByEmployeeId(inactivity.getEmployee().getId());

        for (PbiEmployeeInactivity existing : existingInactivities) {
            if (!existing.getId().equals(inactivity.getId()) &&
                    (inactivity.getStartDate().isBefore(existing.getEndDate()) && inactivity.getEndDate().isAfter(existing.getStartDate()))) {
                return false;
            }
        }
        return true;
    }
}
