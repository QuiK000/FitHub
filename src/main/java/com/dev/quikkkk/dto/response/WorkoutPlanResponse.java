package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.DifficultyLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutPlanResponse {
    private String id;
    private String name;
    private String description;
    private DifficultyLevel difficultyLevel;
    private Integer durationWeeks;
    private Integer sessionsPerWeek;
    private boolean active;
    private TrainerShortResponse trainer;
    private List<WorkoutPlanExerciseResponse> exercises;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
