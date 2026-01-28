package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.response.ExerciseShortResponse;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;
import com.dev.quikkkk.entity.ClientWorkoutPlan;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.WorkoutLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkoutLogMapper {
    public WorkoutLog toEntity(LogWorkoutRequest request, Exercise exercise, ClientWorkoutPlan activeWorkoutPlan, String userId) {
        return WorkoutLog.builder()
                .exercise(exercise)
                .clientWorkoutPlan(activeWorkoutPlan)
                .setsCompleted(request.getSetsCompleted())
                .repsCompleted(request.getRepsCompleted())
                .weightUsed(request.getWeightUsed())
                .durationSeconds(request.getDurationSeconds())
                .difficultyRating(request.getDifficultRating())
                .notes(request.getNotes() != null ? request.getNotes() : null)
                .workoutDate(LocalDateTime.now())
                .createdBy(userId)
                .build();
    }

    public WorkoutLogResponse toResponse(WorkoutLog workoutLog) {
        return WorkoutLogResponse.builder()
                .id(workoutLog.getId())
                .exercise(ExerciseShortResponse.builder()
                        .exerciseId(workoutLog.getExercise().getId())
                        .name(workoutLog.getExercise().getName())
                        .category(workoutLog.getExercise().getCategory())
                        .primaryMuscleGroup(workoutLog.getExercise().getPrimaryMuscleGroup())
                        .imageUrl(workoutLog.getExercise().getImageUrl())
                        .build())
                .workoutDate(workoutLog.getWorkoutDate())
                .setsCompleted(workoutLog.getSetsCompleted())
                .repsCompleted(workoutLog.getRepsCompleted())
                .weightUsed(workoutLog.getWeightUsed())
                .durationSeconds(workoutLog.getDurationSeconds())
                .difficultyRating(workoutLog.getDifficultyRating())
                .notes(workoutLog.getNotes())
                .build();
    }
}
