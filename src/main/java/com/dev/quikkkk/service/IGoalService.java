package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.response.GoalResponse;

public interface IGoalService {
    GoalResponse createGoal(CreateGoalRequest request);
}
