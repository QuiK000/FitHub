package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.AssignWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.ClientWorkoutPlanResponse;
import com.dev.quikkkk.dto.response.TrainerShortResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanShortResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.ClientWorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.enums.ClientWorkoutStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClientWorkoutPlanMapper {
    public ClientWorkoutPlan toEntity(AssignWorkoutPlanRequest request, WorkoutPlan plan, ClientProfile client) {
        return ClientWorkoutPlan.builder()
                .assignedDate(LocalDateTime.now())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ClientWorkoutStatus.ASSIGNED)
                .completionPercentage(0.0)
                .client(client)
                .workoutPlan(plan)
                .createdBy("SYSTEM")
                .build();
    }

    public ClientWorkoutPlanResponse toResponse(ClientWorkoutPlan clientWorkoutPlan) {
        return ClientWorkoutPlanResponse.builder()
                .id(clientWorkoutPlan.getId())
                .workoutPlan(
                        WorkoutPlanShortResponse.builder()
                                .id(clientWorkoutPlan.getWorkoutPlan().getId())
                                .name(clientWorkoutPlan.getWorkoutPlan().getName())
                                .difficultyLevel(clientWorkoutPlan.getWorkoutPlan().getDifficultyLevel())
                                .durationWeeks(clientWorkoutPlan.getWorkoutPlan().getDurationWeeks())
                                .sessionsPerWeek(clientWorkoutPlan.getWorkoutPlan().getSessionsPerWeek())
                                .trainer(
                                        TrainerShortResponse.builder()
                                                .trainerId(clientWorkoutPlan.getWorkoutPlan().getTrainer().getId())
                                                .firstname(clientWorkoutPlan.getWorkoutPlan().getTrainer().getFirstname())
                                                .lastname(clientWorkoutPlan.getWorkoutPlan().getTrainer().getLastname())
                                                .build()
                                )
                                .build()
                )
                .assignedDate(clientWorkoutPlan.getAssignedDate())
                .startDate(clientWorkoutPlan.getStartDate())
                .endDate(clientWorkoutPlan.getEndDate())
                .status(clientWorkoutPlan.getStatus())
                .completionPercentage(clientWorkoutPlan.getCompletionPercentage())
                .totalWorkouts(0)
                .completedWorkouts(0)
                .build();
    }
}
