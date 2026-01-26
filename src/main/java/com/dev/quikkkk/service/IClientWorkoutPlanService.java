package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.AssignWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.ClientWorkoutPlanResponse;
import com.dev.quikkkk.dto.response.PageResponse;

import java.util.List;

public interface IClientWorkoutPlanService {
    ClientWorkoutPlanResponse assignPlanToClient(AssignWorkoutPlanRequest request, String workoutPlanId);

    PageResponse<ClientWorkoutPlanResponse> getAssignedPlans(int page, int size);

    ClientWorkoutPlanResponse getAssignedPlanById(String assignedPlanId);

    List<ClientWorkoutPlanResponse> getMyAssignments();

    List<ClientWorkoutPlanResponse> getMyActiveAssignments();

    ClientWorkoutPlanResponse getMyAssignmentById(String assignmentId);
}
