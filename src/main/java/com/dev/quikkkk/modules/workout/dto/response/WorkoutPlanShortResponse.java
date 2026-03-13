package com.dev.quikkkk.modules.workout.dto.response;

import com.dev.quikkkk.modules.user.dto.response.TrainerShortResponse;
import com.dev.quikkkk.modules.workout.enums.DifficultyLevel;
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
public class WorkoutPlanShortResponse {
    private String id;
    private String name;
    private DifficultyLevel difficultyLevel;
    private Integer durationWeeks;
    private Integer sessionsPerWeek;
    private TrainerShortResponse trainer;
}
