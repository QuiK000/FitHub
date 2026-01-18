package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.ExerciseShortResponse;
import com.dev.quikkkk.dto.response.TrainerShortResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanExerciseResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlanExercise;
import org.springframework.stereotype.Service;

@Service
public class WorkoutPlanMapper {

    public WorkoutPlan toEntity(CreateWorkoutPlanRequest request, TrainerProfile trainer) {
        return WorkoutPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .difficultyLevel(request.getDifficultyLevel())
                .durationWeeks(request.getDurationWeeks())
                .sessionsPerWeek(request.getSessionsPerWeek())
                .active(true)
                .trainer(trainer)
                .createdBy(trainer.getId())
                .build();
    }

    public WorkoutPlanResponse toResponse(WorkoutPlan plan) {
        return WorkoutPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .difficultyLevel(plan.getDifficultyLevel())
                .durationWeeks(plan.getDurationWeeks())
                .sessionsPerWeek(plan.getSessionsPerWeek())
                .active(plan.isActive())
                .trainer(
                        TrainerShortResponse.builder()
                                .trainerId(plan.getTrainer().getId())
                                .firstname(plan.getTrainer().getFirstname())
                                .lastname(plan.getTrainer().getLastname())
                                .build()
                )
                .exercises(
                        plan.getExercises().stream()
                                .map(this::mapExercise)
                                .toList()
                )
                .createdAt(plan.getCreatedDate())
                .build();
    }

    private WorkoutPlanExerciseResponse mapExercise(WorkoutPlanExercise wpe) {
        return WorkoutPlanExerciseResponse.builder()
                .id(wpe.getId())
                .exercise(
                        ExerciseShortResponse.builder()
                                .exerciseId(wpe.getExercise().getId())
                                .name(wpe.getExercise().getName())
                                .category(wpe.getExercise().getCategory())
                                .primaryMuscleGroup(wpe.getExercise().getPrimaryMuscleGroup())
                                .imageUrl(wpe.getExercise().getImageUrl())
                                .build()
                )
                .dayNumber(wpe.getDayNumber())
                .orderIndex(wpe.getOrderIndex())
                .sets(wpe.getSets())
                .reps(wpe.getReps())
                .durationSeconds(wpe.getDurationSeconds())
                .restSeconds(wpe.getRestSeconds())
                .notes(wpe.getNotes())
                .build();
    }
}
