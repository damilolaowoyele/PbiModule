package org.fhi360.PbiModule.Model.PbiEmployeeInactivity;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoOverlapValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoOverlap {
    String message() default "Overlapping periods of inactivity are not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
