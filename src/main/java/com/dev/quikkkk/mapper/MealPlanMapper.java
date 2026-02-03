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
                .targetMacros(request.getTargetMacros() != null
                        ? MacroNutrients.builder()
                        .protein(request.getTargetMacros().getProtein())
                        .sugar(request.getTargetMacros().getSugar())
                        .carbs(request.getTargetMacros().getCarbs())
                        .fiber(request.getTargetMacros().getFiber())
                        .fats(request.getTargetMacros().getFats())
                        .build()
                        : null
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
                                .protein(mealPlan.getMacros().getProtein() != null ? mealPlan.getMacros().getProtein() : null)
                                .carbs(mealPlan.getMacros().getCarbs() != null ? mealPlan.getMacros().getCarbs() : null)
                                .fiber(mealPlan.getMacros().getFiber() != null ? mealPlan.getMacros().getFiber() : null)
                                .fats(mealPlan.getMacros().getFats() != null ? mealPlan.getMacros().getFats() : null)
                                .sugar(mealPlan.getMacros().getSugar() != null ? mealPlan.getMacros().getSugar() : null)
                                .build()
                )
                .targetMacros(
                        MacroNutrientsDto.builder()
                                .protein(mealPlan.getTargetMacros().getProtein() != null ? mealPlan.getTargetMacros().getProtein() : null)
                                .carbs(mealPlan.getTargetMacros().getCarbs() != null ? mealPlan.getTargetMacros().getCarbs() : null)
                                .fiber(mealPlan.getTargetMacros().getFiber() != null ? mealPlan.getTargetMacros().getFiber() : null)
                                .fats(mealPlan.getTargetMacros().getFats() != null ? mealPlan.getTargetMacros().getFats() : null)
                                .sugar(mealPlan.getTargetMacros().getSugar() != null ? mealPlan.getTargetMacros().getSugar() : null)
                                .build())
                .meals(null)
                .notes(mealPlan.getNotes())
                .caloriesPercentage(0.0)
                .completed(false)
                .build();
    }
}
