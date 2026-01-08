package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTrainerProfileRequest {
    @NotBlank(message = "VALIDATION.CREATE.TRAINER.PROFILE.FIRST.NAME.NOT_BLANK")
    private String firstname;

    @NotBlank(message = "VALIDATION.CREATE.TRAINER.PROFILE.LAST.NAME.NOT_BLANK")
    private String lastname;

    @NotEmpty(message = "VALIDATION.CREATE.TRAINER.PROFILE.SPECIALIZATION.NOT_NULL")
    private Set<String> specializationIds = new HashSet<>();

    @Positive(message = "VALIDATION.CREATE.TRAINER.PROFILE.EXPERIENCE.YEARS.MUST.BE.POSITIVE")
    @NotNull(message = "VALIDATION.CREATE.TRAINER.PROFILE.EXPERIENCE.YEARS.NOT.BLANK")
    private int experienceYears;

    @NotBlank(message = "VALIDATION.CREATE.TRAINER.PROFILE.DESCRIPTION.NOT.BLANK")
    private String description;
}
