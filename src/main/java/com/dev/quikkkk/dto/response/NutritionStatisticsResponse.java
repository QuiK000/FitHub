package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.dto.request.MacroNutrientsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NutritionStatisticsResponse {
    private Integer totalMealsLogged;
    private Double averageCalories;
    private MacroNutrientsDto averageMacros;
    private Integer currentStreak;
    private Integer longestStreak;
    private Double averageWaterIntake;
    private List<DailyCaloriesDto> weeklyCalories;
}
