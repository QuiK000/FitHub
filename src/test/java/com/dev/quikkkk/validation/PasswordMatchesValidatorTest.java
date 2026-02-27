package com.dev.quikkkk.validation;

import com.dev.quikkkk.dto.request.RegistrationRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordMatchesValidatorTest {

    private final PasswordMatchesValidator validator = new PasswordMatchesValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @Test
    @DisplayName("Should return true when passwords match")
    void isValid_WhenPasswordsMatch_ReturnsTrue() {
        RegistrationRequest request = RegistrationRequest.builder()
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();

        boolean result = validator.isValid(request, context);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when passwords do not match")
    void isValid_WhenPasswordsDoNotMatch_ReturnsFalse() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        RegistrationRequest request = RegistrationRequest.builder()
                .password("Password123!")
                .confirmPassword("DifferentPassword!")
                .build();

        boolean result = validator.isValid(request, context);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when one password is null")
    void isValid_WhenPasswordIsNull_ReturnsFalse() {
        RegistrationRequest request = RegistrationRequest.builder()
                .password(null)
                .confirmPassword("Password123!")
                .build();

        boolean result = validator.isValid(request, context);

        assertFalse(result);
    }
}