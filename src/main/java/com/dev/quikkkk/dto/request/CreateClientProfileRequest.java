package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.ClientGender;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @Positive(message = "VALIDATION.CREATE.CLIENT.PROFILE.HEIGHT.POSITIVE")
    private Double height;

    @Positive(message = "VALIDATION.CREATE.CLIENT.PROFILE.WEIGHT.POSITIVE")
    private Double weight;

    @Positive(message = "VALIDATION.CREATE.CLIENT.PROFILE.DAILY_WATER_TARGET.POSITIVE")
    private Integer dailyWaterTarget;

    @NotNull(message = "VALIDATION.CREATE.CLIENT.PROFILE.GENDER.NOT_NULL")
    private ClientGender gender;
}
