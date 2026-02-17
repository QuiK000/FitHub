package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.dto.request.UpdateGoalRequest;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;

public interface IGoalService {
    GoalResponse createGoal(CreateGoalRequest request);

    PageResponse<GoalResponse> getGoals(int page, int size);

    GoalResponse getGoalById(String goalId);

    GoalResponse updateGoalById(String goalId, UpdateGoalRequest request);

    GoalResponse updateGoalProgress(String goalId, UpdateGoalProgressRequest request);

    MessageResponse completeGoal(String goalId);

    PageResponse<GoalResponse> getActiveGoals(int page, int size);

    PageResponse<GoalResponse> getCompletedGoals(int page, int size);
}
