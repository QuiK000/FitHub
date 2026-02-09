package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.validation.ValidationPatterns;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UpdateClientProfileRequest {
    private String firstname;
    private String lastname;

    @Pattern(
            regexp = ValidationPatterns.PHONE_E164,
            message = "VALIDATION.CREATE.CLIENT.PROFILE.PHONE.INVALID"
    )
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @Positive(message = "VALIDATION.UPDATE.CLIENT.PROFILE.HEIGHT.POSITIVE")
    private Double height;

    @Positive(message = "VALIDATION.UPDATE.CLIENT.PROFILE.WEIGHT.POSITIVE")
    private Double weight;

    @Positive(message = "VALIDATION.UPDATE.CLIENT.PROFILE.DAILY_WATER_TARGET.POSITIVE")
    private Integer dailyWaterTarget;
}
