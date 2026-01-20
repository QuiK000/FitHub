package com.dev.quikkkk.dto.request;

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
public class ResetPasswordRequest {
    @NotBlank(message = "VALIDATION.RESET_PASSWORD.TOKEN.NOT_BLANK")
    private String token;

    @Size(min = 8, max = 50, message = "VALIDATION.RESET_PASSWORD.PASSWORD.SIZE")
    @Pattern(regexp = ValidationPatterns.PASSWORD_STRONG_REGEX, message = "VALIDATION.RESET_PASSWORD.PASSWORD.WEAK")
    @NotBlank(message = "VALIDATION.RESET_PASSWORD.PASSWORD.NOT_BLANK")
    private String password;

    @NotBlank(message = "VALIDATION.RESET_PASSWORD.CONFIRM_PASSWORD.NOT_BLANK")
    @Size(min = 8, max = 50, message = "VALIDATION.RESET_PASSWORD.CONFIRM_PASSWORD.SIZE")
    private String confirmPassword;
}
