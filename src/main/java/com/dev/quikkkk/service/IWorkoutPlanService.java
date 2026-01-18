package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;

public interface IWorkoutPlanService {
    WorkoutPlanResponse createWorkoutPlan(CreateWorkoutPlanRequest request);
}
