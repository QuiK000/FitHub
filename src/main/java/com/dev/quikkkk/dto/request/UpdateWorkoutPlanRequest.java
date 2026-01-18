package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.DifficultyLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class UpdateWorkoutPlanRequest {
    @Size(max = 200, message = "VALIDATION.WORKOUT_PLAN.NAME.SIZE")
    private String name;
    private String description;
    private DifficultyLevel difficultyLevel;
    @Positive(message = "VALIDATION.WORKOUT_PLAN.DURATION.POSITIVE")
    private Integer durationWeeks;
    @Min(value = 1, message = "VALIDATION.WORKOUT_PLAN.SESSIONS.MIN")
    @Max(value = 7, message = "VALIDATION.WORKOUT_PLAN.SESSIONS.MAX")
    private Integer sessionsPerWeek;
}
