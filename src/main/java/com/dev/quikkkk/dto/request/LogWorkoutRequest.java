package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LogWorkoutRequest {
    @NotBlank(message = "VALIDATION.WORKOUT_LOG.EXERCISE_ID.NOT_BLANK")
    private String exerciseId;

    @Min(value = 1, message = "VALIDATION.WORKOUT_LOG.SETS.MIN")
    private Integer setsCompleted;

    @Min(value = 1, message = "VALIDATION.WORKOUT_LOG.REPS.MIN")
    private Integer repsCompleted;

    @PositiveOrZero(message = "VALIDATION.WORKOUT_LOG.WEIGHT.POSITIVE")
    private Double weightUsed;

    @PositiveOrZero(message = "VALIDATION.WORKOUT_LOG.DURATION.POSITIVE")
    private Integer durationSeconds;

    @Min(value = 1, message = "VALIDATION.WORKOUT_LOG.DIFFICULTY.MIN")
    @Max(value = 5, message = "VALIDATION.WORKOUT_LOG.DIFFICULTY.MAX")
    private Integer difficultRating;
    private String notes;
}
