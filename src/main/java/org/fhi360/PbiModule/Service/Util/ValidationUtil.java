package org.fhi360.PbiModule.Service.Util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {
    public static <T> void validate(T object, Validator validator) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Validation failed: " + errorMessage);
        }
    }


//    public static <T> void validate(T object, Validator validator) {
//        Set<ConstraintViolation<T>> violations = validator.validate(object);
//        if (!violations.isEmpty()) {
//            throw new RuntimeException("Validation failed for " + object.getClass().getSimpleName());
//        }
//    }
}
