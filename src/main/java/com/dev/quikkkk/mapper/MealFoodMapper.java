package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.MealFoodRequest;
import com.dev.quikkkk.entity.Food;
import com.dev.quikkkk.entity.MacroNutrients;
import com.dev.quikkkk.entity.MealFood;
import org.springframework.stereotype.Service;

@Service
public class MealFoodMapper {
    public MealFood toEntity(Food food, MealFoodRequest request) {
        MacroNutrients foodMacros = food.getMacrosPerServing() != null
                ? food.getMacrosPerServing()
                : new MacroNutrients();

        Integer calories = food.getCaloriesPerServing() != null ? food.getCaloriesPerServing() : 0;

        return MealFood.builder()
                .food(food)
                .servings(request.getServings())
                .totalCalories((int) (calories * request.getServings()))
                .totalMacros(
                        MacroNutrients.builder()
                                .protein(safeMultiply(foodMacros.getProtein(), request.getServings()))
                                .carbs(safeMultiply(foodMacros.getCarbs(), request.getServings()))
                                .fats(safeMultiply(foodMacros.getFats(), request.getServings()))
                                .sugar(safeMultiply(foodMacros.getSugar(), request.getServings()))
                                .fiber(safeMultiply(foodMacros.getFiber(), request.getServings()))
                                .build()
                )
                .build();
    }

    private Double safeMultiply(Double value, Double multiplier) {
        if (value == null) return 0.0;
        if (multiplier == null) return 0.0;
        return value * multiplier;
    }
}
