package com.security.fiverr.auth.validation;

import com.security.fiverr.auth.model.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchingValidator implements ConstraintValidator<PasswordMatching, RegisterRequest> {

    @Override
    public void initialize(PasswordMatching constraintAnnotation) {
    }

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        boolean isValid = request.password() != null && 
                         request.password().equals(request.confirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                   .addPropertyNode("confirmPassword")
                   .addConstraintViolation();
        }

        return isValid;
    }
}