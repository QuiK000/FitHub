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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReorderWorkoutPlanExerciseItem {
    @NotBlank(message = "VALIDATION.WORKOUT_PLAN.EXERCISE.ID.NOT_BLANK")
    private String planExerciseId;

    @NotNull(message = "VALIDATION.WORKOUT_PLAN.EXERCISE.ORDER.NOT_NULL")
    @Min(value = 0, message = "VALIDATION.WORKOUT_PLAN.EXERCISE.ORDER.MIN")
    private Integer orderIndex;
}
