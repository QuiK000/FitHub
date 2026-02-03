package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.request.MacroNutrientsDto;
import com.dev.quikkkk.dto.response.MealPlanResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.MacroNutrients;
import com.dev.quikkkk.entity.MealPlan;
import org.springframework.stereotype.Service;

@Service
public class MealPlanMapper {
    public MealPlan toEntity(CreateMealPlanRequest request, ClientProfile client) {
        return MealPlan.builder()
                .planDate(request.getPlanDate())
                .targetCalories(request.getTargetCalories())
                .targetMacros(
                        MacroNutrients.builder()
                                .sugar(request.getTargetMacros().getSugar())
                                .carbs(request.getTargetMacros().getCarbs())
                                .fiber(request.getTargetMacros().getFiber())
                                .fats(request.getTargetMacros().getFats())
                                .build()
                )
                .notes(request.getNotes())
                .client(client)
                .createdBy(client.getId())
                .build();
    }

    public MealPlanResponse toResponse(MealPlan mealPlan) {
        return MealPlanResponse.builder()
                .id(mealPlan.getId())
                .planDate(mealPlan.getPlanDate())
                .totalCalories(mealPlan.getTotalCalories())
                .targetCalories(mealPlan.getTargetCalories())
                .macros(
                        MacroNutrientsDto.builder()
                                .protein(mealPlan.getMacros().getProtein())
                                .carbs(mealPlan.getMacros().getCarbs())
                                .fiber(mealPlan.getMacros().getFiber())
                                .fats(mealPlan.getMacros().getFats())
                                .sugar(mealPlan.getMacros().getSugar())
                                .build()
                )
                .targetMacros(
                        MacroNutrientsDto.builder()
                                .protein(mealPlan.getMacros().getProtein())
                                .carbs(mealPlan.getMacros().getCarbs())
                                .fiber(mealPlan.getMacros().getFiber())
                                .fats(mealPlan.getMacros().getFats())
                                .sugar(mealPlan.getMacros().getSugar())
                                .build())
                .meals(null)
                .notes(mealPlan.getNotes())
                .caloriesPercentage(0.0)
                .completed(false)
                .build();
    }
}
