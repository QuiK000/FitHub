package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMealRequest;
import com.dev.quikkkk.dto.request.UpdateMealRequest;
import com.dev.quikkkk.dto.response.MealResponse;
import com.dev.quikkkk.dto.response.MessageResponse;

public interface IMealService {
    MealResponse createMeal(CreateMealRequest request, String mealPlanId);

    MealResponse updateMeal(String mealId, UpdateMealRequest request);

    MessageResponse completeMealById(String mealId);
}
