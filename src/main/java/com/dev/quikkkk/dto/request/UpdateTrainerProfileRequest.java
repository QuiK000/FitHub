package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTrainerProfileRequest {
    private String firstname;
    private String lastname;

    private Set<String> specializationIds;

    @Positive(message = "VALIDATION.CREATE.TRAINER.PROFILE.EXPERIENCE.YEARS.MUST.BE.POSITIVE")
    private Integer experienceYears;
    private String description;
}
