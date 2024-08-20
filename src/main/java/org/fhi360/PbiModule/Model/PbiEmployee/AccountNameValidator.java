package org.fhi360.PbiModule.Model.PbiEmployee;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AccountNameValidator implements ConstraintValidator<ValidAccountName, PbiEmployee> {

    private static final Logger logger = LoggerFactory.getLogger(AccountNameValidator.class);

    @Override
    public void initialize(ValidAccountName constraintAnnotation) {
    }

    @Override
    public boolean isValid(PbiEmployee employee, ConstraintValidatorContext context) {
        if (employee == null) {
            return true; // Handle null case if necessary
        }

        try {
            String accountName = normalizeSpaces(employee.getAccountName()).toLowerCase();
            String firstName = normalizeSpaces(employee.getFirstName()).toLowerCase();
            String middleName = normalizeSpaces(employee.getMiddleName()).toLowerCase();
            String lastName = normalizeSpaces(employee.getLastName()).toLowerCase();

            List<String> validCombinations = Arrays.asList(
                    firstName + " " + middleName + " " + lastName,
                    firstName + " " + lastName + " " + middleName,
                    middleName + " " + firstName + " " + lastName,
                    middleName + " " + lastName + " " + firstName,
                    lastName + " " + firstName + " " + middleName,
                    lastName + " " + middleName + " " + firstName,
                    (firstName.isEmpty() ? "" : firstName.charAt(0) + ". ") + middleName + " " + lastName,
                    (firstName.isEmpty() ? "" : firstName.charAt(0) + ". ") + lastName + " " + middleName,
                    (middleName.isEmpty() ? "" : middleName.charAt(0) + ". ") + firstName + " " + lastName,
                    (middleName.isEmpty() ? "" : middleName.charAt(0) + ". ") + lastName + " " + firstName,
                    (lastName.isEmpty() ? "" : lastName.charAt(0) + ". ") + firstName + " " + middleName,
                    (lastName.isEmpty() ? "" : lastName.charAt(0) + ". ") + middleName + " " + firstName,
                    firstName + " " + (middleName.isEmpty() ? "" : middleName.charAt(0) + ". ") + lastName,
                    firstName + " " + (lastName.isEmpty() ? "" : lastName.charAt(0) + ". ") + middleName,
                    middleName + " " + (firstName.isEmpty() ? "" : firstName.charAt(0) + ". ") + lastName,
                    middleName + " " + (lastName.isEmpty() ? "" : lastName.charAt(0) + ". ") + firstName,
                    lastName + " " + (firstName.isEmpty() ? "" : firstName.charAt(0) + ". ") + middleName,
                    lastName + " " + (middleName.isEmpty() ? "" : middleName.charAt(0) + ". ") + firstName,
                    firstName + " " + middleName + " " + (lastName.isEmpty() ? "" : lastName.charAt(0) + "."),
                    firstName + " " + lastName + " " + (middleName.isEmpty() ? "" : middleName.charAt(0) + "."),
                    middleName + " " + firstName + " " + (lastName.isEmpty() ? "" : lastName.charAt(0) + "."),
                    middleName + " " + lastName + " " + (firstName.isEmpty() ? "" : firstName.charAt(0) + "."),
                    lastName + " " + firstName + " " + (middleName.isEmpty() ? "" : middleName.charAt(0) + "."),
                    lastName + " " + middleName + " " + (firstName.isEmpty() ? "" : firstName.charAt(0) + ".")
            );

            return validCombinations.contains(accountName);
        } catch (Exception e) {
            logger.error("Unexpected exception during isValid call", e);
            return false;
        }
    }

    private String normalizeSpaces(String input) {
        return input == null ? "" : input.trim().replaceAll("\\s+", " ");
    }

//    @Override
//    public boolean isValid(PbiEmployee employee, ConstraintValidatorContext context) {
//        if (employee == null) {
//            return true; // Handle null case if necessary
//        }
//
//        try {
//            String accountName = normalizeSpaces(employee.getAccountName()).toLowerCase();
//            String firstName = normalizeSpaces(employee.getFirstName()).toLowerCase();
//            String middleName = normalizeSpaces(employee.getMiddleName()).toLowerCase();
//            String lastName = normalizeSpaces(employee.getLastName()).toLowerCase();
//
//            List<String> validCombinations = Arrays.asList(
//                    firstName + " " + lastName,
//                    middleName + " " + lastName,
//                    firstName + " " + middleName,
//                    lastName + " " + firstName,
//                    lastName + " " + middleName,
//                    middleName + " " + firstName,
//                    firstName + " " + middleName + " " + lastName,
//                    firstName + " " + lastName + " " + middleName,
//                    middleName + " " + firstName + " " + lastName,
//                    middleName + " " + lastName + " " + firstName,
//                    lastName + " " + firstName + " " + middleName,
//                    lastName + " " + middleName + " " + firstName,
//                    firstName.charAt(0) + ". " + middleName + " " + lastName,
//                    firstName.charAt(0) + ". " + lastName + " " + middleName,
//                    middleName.charAt(0) + ". " + firstName + " " + lastName,
//                    middleName.charAt(0) + ". " + lastName + " " + firstName,
//                    lastName.charAt(0) + ". " + firstName + " " + middleName,
//                    lastName.charAt(0) + ". " + middleName + " " + firstName,
//                    firstName + " " + middleName.charAt(0) + ". " + lastName,
//                    firstName + " " + lastName.charAt(0) + ". " + middleName,
//                    middleName + " " + firstName.charAt(0) + ". " + lastName,
//                    middleName + " " + lastName.charAt(0) + ". " + firstName,
//                    lastName + " " + firstName.charAt(0) + ". " + middleName,
//                    lastName + " " + middleName.charAt(0) + ". " + firstName,
//                    firstName + " " + middleName + " " + lastName.charAt(0) + ".",
//                    firstName + " " + lastName + " " + middleName.charAt(0) + ".",
//                    middleName + " " + firstName + " " + lastName.charAt(0) + ".",
//                    middleName + " " + lastName + " " + firstName.charAt(0) + ".",
//                    lastName + " " + firstName + " " + middleName.charAt(0) + ".",
//                    lastName + " " + middleName + " " + firstName.charAt(0) + "."
//            );
//
//            return validCombinations.contains(accountName);
//        } catch (Exception e) {
//            logger.error("Unexpected exception during isValid call", e);
//            return false;
//        }
//    }
//
//    private String normalizeSpaces(String input) {
//        return input == null ? "" : input.trim().replaceAll("\\s+", " ");
//    }
}