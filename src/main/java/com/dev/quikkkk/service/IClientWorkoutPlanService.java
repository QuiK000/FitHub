package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.AssignWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.ClientWorkoutPlanResponse;
import com.dev.quikkkk.dto.response.PageResponse;

public interface IClientWorkoutPlanService {
    ClientWorkoutPlanResponse assignPlanToClient(AssignWorkoutPlanRequest request, String workoutPlanId);

    PageResponse<ClientWorkoutPlanResponse> getAssignedPlans(int page, int size);
}
