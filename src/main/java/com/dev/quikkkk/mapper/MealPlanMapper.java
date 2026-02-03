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
                .targetMacros(mapToMacroNutrients(request.getTargetMacros()))
                .notes(request.getNotes())
                .totalCalories(0)
                .macros(MacroNutrients.builder()
                        .protein(0.0)
                        .carbs(0.0)
                        .fats(0.0)
                        .fiber(0.0)
                        .sugar(0.0)
                        .build())
                .client(client)
                .createdBy(client.getId())
                .build();
    }

    public MealPlanResponse toResponse(MealPlan mealPlan) {
        Integer targetCalories = mealPlan.getTargetCalories();

        int totalCalories = mealPlan.getTotalCalories() != null ? mealPlan.getTotalCalories() : 0;
        double caloriesPercentage = 0.0;

        if (targetCalories != null && targetCalories > 0)
            caloriesPercentage = ((double) totalCalories / targetCalories) * 100;

        return MealPlanResponse.builder()
                .id(mealPlan.getId())
                .planDate(mealPlan.getPlanDate())
                .totalCalories(totalCalories)
                .targetCalories(targetCalories)
                .macros(mapToMacroNutrientsDto(mealPlan.getMacros()))
                .targetMacros(mapToMacroNutrientsDto(mealPlan.getTargetMacros()))
                .meals(null)
                .notes(mealPlan.getNotes())
                .caloriesPercentage(caloriesPercentage)
                .completed(totalCalories >= (targetCalories != null ? targetCalories : Integer.MAX_VALUE))
                .build();
    }

    private MacroNutrients mapToMacroNutrients(MacroNutrientsDto dto) {
        if (dto == null)
            return MacroNutrients.builder()
                    .protein(0.0)
                    .carbs(0.0)
                    .fats(0.0)
                    .fiber(0.0)
                    .sugar(0.0)
                    .build();

        return MacroNutrients.builder()
                .protein(dto.getProtein() != null ? dto.getProtein() : 0.0)
                .carbs(dto.getCarbs() != null ? dto.getCarbs() : 0.0)
                .fats(dto.getFats() != null ? dto.getFats() : 0.0)
                .fiber(dto.getFiber() != null ? dto.getFiber() : 0.0)
                .sugar(dto.getSugar() != null ? dto.getSugar() : 0.0)
                .build();
    }

    private MacroNutrientsDto mapToMacroNutrientsDto(MacroNutrients macros) {
        if (macros == null)
            return MacroNutrientsDto.builder()
                    .protein(0.0)
                    .carbs(0.0)
                    .fats(0.0)
                    .fiber(0.0)
                    .sugar(0.0)
                    .build();

        return MacroNutrientsDto.builder()
                .protein(macros.getProtein() != null ? macros.getProtein() : 0.0)
                .carbs(macros.getCarbs() != null ? macros.getCarbs() : 0.0)
                .fats(macros.getFats() != null ? macros.getFats() : 0.0)
                .fiber(macros.getFiber() != null ? macros.getFiber() : 0.0)
                .sugar(macros.getSugar() != null ? macros.getSugar() : 0.0)
                .build();
    }
}
