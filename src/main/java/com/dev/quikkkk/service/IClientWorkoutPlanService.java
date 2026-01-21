package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.AssignWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.ClientWorkoutPlanResponse;

public interface IClientWorkoutPlanService {
    ClientWorkoutPlanResponse assignPlanToClient(AssignWorkoutPlanRequest request, String workoutPlanId);
}
