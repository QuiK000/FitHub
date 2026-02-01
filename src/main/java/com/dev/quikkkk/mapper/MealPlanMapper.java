package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.entity.MacroNutrients;
import com.dev.quikkkk.entity.MealPlan;
import org.springframework.stereotype.Service;

@Service
public class MealPlanMapper {
    public MealPlan toEntity(CreateMealPlanRequest request) {
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
                .build();
    }
}
