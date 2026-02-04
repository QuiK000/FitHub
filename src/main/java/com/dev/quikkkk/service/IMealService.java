package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMealRequest;
import com.dev.quikkkk.dto.response.MealResponse;

public interface IMealService {
    MealResponse createMeal(CreateMealRequest request, String mealPlanId);
}
