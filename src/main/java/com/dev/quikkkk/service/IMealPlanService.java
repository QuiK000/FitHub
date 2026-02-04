package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.request.UpdateMealPlanRequest;
import com.dev.quikkkk.dto.response.MealPlanResponse;
import com.dev.quikkkk.dto.response.PageResponse;

import java.time.LocalDate;

public interface IMealPlanService {
    MealPlanResponse createMealPlan(CreateMealPlanRequest request);

    PageResponse<MealPlanResponse> getMyMealPlans(int page, int size);

    PageResponse<MealPlanResponse> getMealPlansByDateRange(LocalDate startDate, LocalDate endDate);

    MealPlanResponse getMealPlanByDate(LocalDate date);

    MealPlanResponse getMealPlanById(String mealPlanId);

    MealPlanResponse updateMealPlan(UpdateMealPlanRequest request, String mealPlanId);
}
