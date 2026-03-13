package com.dev.quikkkk.modules.progress.service;

import com.dev.quikkkk.modules.progress.dto.request.CreateGoalRequest;
import com.dev.quikkkk.modules.progress.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.modules.progress.dto.request.UpdateGoalRequest;
import com.dev.quikkkk.modules.progress.dto.response.GoalResponse;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;

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
