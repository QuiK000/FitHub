package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.response.MealPlanResponse;
import com.dev.quikkkk.dto.response.PageResponse;

public interface IMealPlanService {
    MealPlanResponse createMealPlan(CreateMealPlanRequest request);

    PageResponse<MealPlanResponse> getMyMealPlans(int page, int size);
}
