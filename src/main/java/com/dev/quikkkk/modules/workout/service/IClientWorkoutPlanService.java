package com.dev.quikkkk.modules.workout.service;

import com.dev.quikkkk.modules.workout.dto.request.AssignWorkoutPlanRequest;
import com.dev.quikkkk.modules.workout.dto.response.ClientWorkoutPlanResponse;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;

import java.util.List;

public interface IClientWorkoutPlanService {
    ClientWorkoutPlanResponse assignPlanToClient(AssignWorkoutPlanRequest request, String workoutPlanId);

    PageResponse<ClientWorkoutPlanResponse> getAssignedPlans(int page, int size);

    ClientWorkoutPlanResponse getAssignedPlanById(String assignedPlanId);

    List<ClientWorkoutPlanResponse> getMyAssignments();

    List<ClientWorkoutPlanResponse> getMyActiveAssignments();

    ClientWorkoutPlanResponse getMyAssignmentById(String assignmentId);

    MessageResponse startAssignment(String assignmentId);

    MessageResponse completeAssignment(String assignmentId);

    MessageResponse cancelAssignment(String assignmentId);
}
