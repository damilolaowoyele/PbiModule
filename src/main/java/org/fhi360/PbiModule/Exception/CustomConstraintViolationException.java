package org.fhi360.PbiModule.Exception;

import jakarta.validation.ConstraintViolationException;

import java.util.Set;

public class CustomConstraintViolationException extends ConstraintViolationException {
    public CustomConstraintViolationException(String message, Set<? extends jakarta.validation.ConstraintViolation<?>> violations) {
        super(message, violations);
    }
}
