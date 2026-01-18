package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.DifficultyLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkoutPlanRequest {
    @NotBlank(message = "VALIDATION.WORKOUT_PLAN.NAME.NOT_BLANK")
    @Size(max = 200, message = "VALIDATION.WORKOUT_PLAN.NAME.SIZE")
    private String name;

    @NotBlank(message = "VALIDATION.WORKOUT_PLAN.DESCRIPTION.NOT_BLANK")
    private String description;

    @NotNull(message = "VALIDATION.WORKOUT_PLAN.DIFFICULTY.NOT_NULL")
    private DifficultyLevel difficultyLevel;

    @Positive(message = "VALIDATION.WORKOUT_PLAN.DURATION.POSITIVE")
    private Integer durationWeeks;

    @Min(value = 1, message = "VALIDATION.WORKOUT_PLAN.SESSIONS.MIN")
    @Max(value = 7, message = "VALIDATION.WORKOUT_PLAN.SESSIONS.MAX")
    private Integer sessionsPerWeek;

    @NotEmpty(message = "VALIDATION.WORKOUT_PLAN.EXERCISES.NOT_EMPTY")
    private List<WorkoutExerciseDetailsRequest> exercises = new ArrayList<>();
}
