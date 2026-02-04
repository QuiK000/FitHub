package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.MealFoodRequest;
import com.dev.quikkkk.entity.Food;
import com.dev.quikkkk.entity.MacroNutrients;
import com.dev.quikkkk.entity.MealFood;
import org.springframework.stereotype.Service;

@Service
public class MealFoodMapper {
    public MealFood toEntity(Food food, MealFoodRequest request) {
        return MealFood.builder()
                .food(food)
                .totalCalories(food.getCaloriesPerServing() * request.getServings())
                .totalMacros(
                        MacroNutrients.builder()
                                .protein(food.getMacrosPerServing().getProtein() *  request.getServings())
                                .carbs(food.getMacrosPerServing().getCarbs() *  request.getServings())
                                .fats(food.getMacrosPerServing().getFats() *  request.getServings())
                                .sugar(food.getMacrosPerServing().getSugar() *  request.getServings())
                                .fiber(food.getMacrosPerServing().getFiber() *  request.getServings())
                                .build()
                )
                .build();
    }
}
