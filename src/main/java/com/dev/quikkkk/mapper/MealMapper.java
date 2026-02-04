package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateMealRequest;
import com.dev.quikkkk.entity.Meal;
import com.dev.quikkkk.entity.MealFood;
import com.dev.quikkkk.entity.MealPlan;
import org.springframework.stereotype.Service;

@Service
public class MealMapper {
    public Meal toEntity(CreateMealRequest request, MealPlan mealPlan, MealFood mealFood) {
        return Meal.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getMealType())
                .mealTime(request.getMealTime())
                .mealPlan(mealPlan)
                .completed(false)
                .build();
    }
}
