package org.fhi360.PbiModule.Model.PbiEmployee;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountNameValidator.class)
public @interface ValidAccountName {
    String message() default "Invalid account name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
