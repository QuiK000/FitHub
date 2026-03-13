package com.dev.quikkkk.modules.workout.service;

import com.dev.quikkkk.modules.workout.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.modules.workout.dto.request.UpdateWorkoutPlanRequest;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.workout.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.modules.workout.enums.DifficultyLevel;

public interface IWorkoutPlanService {
    WorkoutPlanResponse createWorkoutPlan(CreateWorkoutPlanRequest request);

    PageResponse<WorkoutPlanResponse> getAllWorkoutPlans(int page, int size, DifficultyLevel difficulty);

    WorkoutPlanResponse getWorkoutPlanById(String workoutPlanId);

    WorkoutPlanResponse updateWorkoutById(String workoutPlanId, UpdateWorkoutPlanRequest request);

    PageResponse<WorkoutPlanResponse> getMyPlans(int page, int size);

    PageResponse<WorkoutPlanResponse> getTrainerPlans(int page, int size, String trainerId);

    MessageResponse activateWorkoutPlan(String workoutPlanId);

    MessageResponse deactivateWorkoutPlan(String workoutPlanId);
}
