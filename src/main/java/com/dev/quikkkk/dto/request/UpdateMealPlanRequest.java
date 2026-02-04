package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMealPlanRequest {
    @Positive(message = "VALIDATION.MEAL_PLAN.TARGET_CALORIES.POSITIVE")
    private Integer targetCalories;

    private MacroNutrientsDto targetMacros;

    private String notes;
}
