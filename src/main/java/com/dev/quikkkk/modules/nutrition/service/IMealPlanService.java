package com.dev.quikkkk.modules.nutrition.service;

import com.dev.quikkkk.modules.nutrition.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.modules.nutrition.dto.request.UpdateMealPlanRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.MealPlanResponse;
import com.dev.quikkkk.core.dto.PageResponse;

import java.time.LocalDate;

public interface IMealPlanService {
    MealPlanResponse createMealPlan(CreateMealPlanRequest request);

    PageResponse<MealPlanResponse> getMyMealPlans(int page, int size);

    PageResponse<MealPlanResponse> getMealPlansByDateRange(LocalDate startDate, LocalDate endDate);

    MealPlanResponse getMealPlanByDate(LocalDate date);

    MealPlanResponse getMealPlanById(String mealPlanId);

    MealPlanResponse updateMealPlan(UpdateMealPlanRequest request, String mealPlanId);
}
