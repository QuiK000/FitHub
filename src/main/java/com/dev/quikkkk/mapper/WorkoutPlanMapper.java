package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.request.UpdateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.TrainerShortResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.WorkoutPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutPlanMapper {
    private final WorkoutPlanExerciseMapper workoutPlanExerciseMapper;

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
                        plan.getExercises()
                                .stream()
                                .map(workoutPlanExerciseMapper::toResponse)
                                .toList()
                )
                .createdAt(plan.getCreatedDate())
                .build();
    }

    public void update(UpdateWorkoutPlanRequest request, WorkoutPlan plan) {
        if (request.getName() != null) plan.setName(request.getName());
        if (request.getDescription() != null) plan.setDescription(request.getDescription());
        if (request.getDifficultyLevel() != null) plan.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getDurationWeeks() != null) plan.setDurationWeeks(request.getDurationWeeks());
        if (request.getSessionsPerWeek() != null) plan.setSessionsPerWeek(request.getSessionsPerWeek());
        plan.setLastModifiedBy(plan.getTrainer().getId());
    }
}
