package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutExerciseDetailsRequest {
    @NotBlank(message = "VALIDATION.EXERCISE.ID.NOT_BLANK")
    private String exerciseId;

    @NotNull(message = "VALIDATION.EXERCISE.DAY.NOT_NULL")
    @Min(value = 1, message = "VALIDATION.EXERCISE.DAY.MIN")
    private Integer dayNumber;

    @NotNull(message = "VALIDATION.EXERCISE.ORDER.NOT_NULL")
    @Min(value = 0, message = "VALIDATION.EXERCISE.ORDER.MIN")
    private Integer orderIndex;
    private Integer sets;
    private Integer reps;
    private Integer durationSeconds;
    private Integer restSeconds;
    private String notes;
}
