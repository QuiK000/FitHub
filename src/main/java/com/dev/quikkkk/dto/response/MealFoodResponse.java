package com.dev.quikkkk.dto.response;

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
public class MealFoodResponse {
    private String id;
    private FoodShortResponse food;
    private Double servings;
    private Integer totalCalories;
    private Integer totalMacros;
}
