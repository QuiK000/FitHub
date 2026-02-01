package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.response.MealPlanResponse;

public interface IMealPlanService {
    MealPlanResponse createMealPlan(CreateMealPlanRequest request);
}
