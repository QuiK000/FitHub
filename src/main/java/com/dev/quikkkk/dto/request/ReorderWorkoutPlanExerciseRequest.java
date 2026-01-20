package com.dev.quikkkk.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReorderWorkoutPlanExerciseRequest {
    @NotNull(message = "VALIDATION.WORKOUT_PLAN.EXERCISE.DAY.NOT_NULL")
    @Min(value = 1, message = "VALIDATION.WORKOUT_PLAN.EXERCISE.DAY.MIN")
    private Integer day;

    @NotEmpty(message = "VALIDATION.WORKOUT_PLAN.EXERCISE.REORDER.NOT_EMPTY")
    @Valid
    private List<ReorderWorkoutPlanExerciseItem> exercises;
}
