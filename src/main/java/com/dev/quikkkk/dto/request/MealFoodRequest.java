package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class MealFoodRequest {
    @NotBlank(message = "VALIDATION.MEAL_FOOD.FOOD_ID.NOT_BLANK")
    private String foodId;

    @Positive(message = "VALIDATION.MEAL_FOOD.SERVINGS.POSITIVE")
    private Integer servings;
}
