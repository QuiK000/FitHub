package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.validation.ValidationPatterns;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateClientProfileRequest {
    @NotBlank(message = "VALIDATION.CREATE.CLIENT.PROFILE.FIRST.NAME.NOT_BLANK")
    private String firstname;

    @NotBlank(message = "VALIDATION.CREATE.CLIENT.PROFILE.LAST.NAME.NOT_BLANK")
    private String lastname;

    @NotBlank(message = "VALIDATION.CREATE.CLIENT.PROFILE.PHONE.NOT_BLANK")
    @Pattern(
            regexp = ValidationPatterns.PHONE_E164,
            message = "VALIDATION.CREATE.CLIENT.PROFILE.PHONE.INVALID"
    )
    private String phone;

    @NotNull(message = "VALIDATION.CREATE.CLIENT.PROFILE.BIRTHDATE.NOT_NULL")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @NotNull(message = "VALIDATION.CREATE.CLIENT.PROFILE.HEIGHT.NOT_NULL")
    @Positive(message = "HEIGHT_MUST_BE_POSITIVE")
    private Double height;

    @NotNull(message = "VALIDATION.CREATE.CLIENT.PROFILE.WEIGHT.NOT_NULL")
    @Positive(message = "WEIGHT_MUST_BE_POSITIVE")
    private Double weight;
}
