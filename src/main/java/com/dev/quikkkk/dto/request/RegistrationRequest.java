package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.validation.NonDisposableEmail;
import com.dev.quikkkk.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {
    @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.NOT_BLANK")
    @Size(max = 155, message = "VALIDATION.REGISTRATION.EMAIL.SIZE")
    @Pattern(regexp = ValidationPatterns.EMAIL_BASIC_REGEX, message = "VALIDATION.REGISTRATION.EMAIL.INVALID")
    @NonDisposableEmail(message = "VALIDATION.REGISTRATION.EMAIL.DISPOSABLE")
    private String email;

    @Size(min = 8, max = 50, message = "VALIDATION.REGISTRATION.PASSWORD.SIZE")
    @Pattern(regexp = ValidationPatterns.PASSWORD_STRONG_REGEX, message = "VALIDATION.REGISTRATION.PASSWORD.WEAK")
    private String password;

    @NotBlank(message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.NOT_BLANK")
    @Size(min = 8, max = 50, message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.SIZE")
    private String confirmPassword;
}
