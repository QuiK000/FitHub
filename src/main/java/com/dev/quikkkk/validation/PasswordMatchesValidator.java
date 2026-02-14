package com.dev.quikkkk.validation;

import com.dev.quikkkk.dto.request.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationRequest> {
    @Override
    public boolean isValid(RegistrationRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getConfirmPassword() == null) return false;
        boolean isValid = request.getPassword().equals(request.getConfirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("VALIDATION.REGISTRATION.PASSWORD.MISMATCH")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
