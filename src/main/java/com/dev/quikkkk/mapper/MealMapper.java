package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateMealRequest;
import com.dev.quikkkk.dto.request.MacroNutrientsDto;
import com.dev.quikkkk.dto.response.FoodShortResponse;
import com.dev.quikkkk.dto.response.MealFoodResponse;
import com.dev.quikkkk.dto.response.MealResponse;
import com.dev.quikkkk.entity.Meal;
import com.dev.quikkkk.entity.MealFood;
import org.springframework.stereotype.Service;

@Service
public class MealMapper {
    public Meal toEntity(CreateMealRequest request, String userId) {
        return Meal.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getMealType())
                .mealTime(request.getMealTime())
                .createdBy(userId)
                .build();
    }

    public MealResponse toResponse(Meal meal) {
        return MealResponse.builder()
                .id(meal.getId())
                .mealType(meal.getType())
                .mealTime(meal.getMealTime())
                .name(meal.getName())
                .description(meal.getDescription())
                .calories(meal.getCalories())
                .macros(MacroNutrientsDto.builder()
                        .sugar(meal.getMacros().getSugar())
                        .protein(meal.getMacros().getProtein())
                        .fats(meal.getMacros().getFats())
                        .fiber(meal.getMacros().getFiber())
                        .carbs(meal.getMacros().getCarbs())
                        .build())
                .foods(meal.getFoods().stream()
                        .map(this::toMealFoodResponse)
                        .toList())
                .completed(meal.isCompleted())
                .build();
    }

    private MealFoodResponse toMealFoodResponse(MealFood mealFood) {
        return MealFoodResponse.builder()
                .id(mealFood.getId())
                .food(FoodShortResponse.builder()
                        .id(mealFood.getFood().getId())
                        .name(mealFood.getFood().getName())
                        .brand(mealFood.getFood().getBrand())
                        .servingUnit(mealFood.getFood().getServingUnit())
                        .build())
                .servings(mealFood.getServings())
                .totalCalories(mealFood.getTotalCalories())
                .totalMacros(null) // TODO
                .build();
    }
}
