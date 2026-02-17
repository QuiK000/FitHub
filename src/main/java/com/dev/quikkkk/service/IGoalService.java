package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.dto.response.MessageResponse;

public interface IGoalService {
    GoalResponse createGoal(CreateGoalRequest request);

    GoalResponse getGoalById(String goalId);

    GoalResponse updateGoalById(String goalId, UpdateGoalProgressRequest request);

    MessageResponse completeGoal(String goalId);
}
