package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMealPlanRequest {
    @NotNull(message = "VALIDATION.MEAL_PLAN.DATE.NOT_NULL")
    private LocalDate planDate;

    @Positive(message = "VALIDATION.MEAL_PLAN.TARGET_CALORIES.POSITIVE")
    private Integer targetCalories;

    private MacroNutrientsDto targetMacros;
    private String notes;
}
