package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.dto.request.MacroNutrientsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanResponse {
    private String id;
    private LocalDate planDate;
    private Integer totalCalories;
    private Integer targetCalories;
    private MacroNutrientsDto macros;
    private MacroNutrientsDto targetMacros;
    private Set<MealResponse> meals;
    private String notes;
    private Double caloriesPercentage;
    private boolean completed;
}
