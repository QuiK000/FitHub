package com.dev.quikkkk.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WorkoutLogResponse {
    private String id;
    private ExerciseShortResponse exercise;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime workoutDate;
    private Integer setsCompleted;
    private Integer repsCompleted;
    private Double weightUsed;
    private Integer durationSeconds;
    private Integer difficultyRating;
    private String notes;
}
