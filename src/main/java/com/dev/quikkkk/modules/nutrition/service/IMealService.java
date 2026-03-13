package com.dev.quikkkk.modules.nutrition.service;

import com.dev.quikkkk.modules.nutrition.dto.request.CreateMealRequest;
import com.dev.quikkkk.modules.nutrition.dto.request.UpdateMealRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.MealResponse;
import com.dev.quikkkk.core.dto.MessageResponse;

public interface IMealService {
    MealResponse createMeal(CreateMealRequest request, String mealPlanId);

    MealResponse updateMeal(String mealId, UpdateMealRequest request);

    MessageResponse completeMealById(String mealId);
}
