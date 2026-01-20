package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpdatePlanExerciseRequest {
    @Min(value = 1, message = "VALIDATION.EXERCISE.SETS.MIN")
    private Integer sets;

    @Min(value = 1, message = "VALIDATION.EXERCISE.REPS.MIN")
    private Integer reps;

    @Min(value = 0, message = "VALIDATION.EXERCISE.DURATION.MIN")
    private Integer durationSeconds;

    @Min(value = 0, message = "VALIDATION.EXERCISE.REST.MIN")
    private Integer restSeconds;
    private String notes;
}
