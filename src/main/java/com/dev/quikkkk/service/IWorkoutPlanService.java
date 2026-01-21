package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.request.UpdateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.enums.DifficultyLevel;

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
