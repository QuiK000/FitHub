package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.ServingUnit;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class UpdateFoodRequest {
    @Size(max = 200, message = "VALIDATION.FOOD.NAME.SIZE")
    private String name;

    @Size(max = 200, message = "VALIDATION.FOOD.BRAND.SIZE")
    private String brand;

    @Positive(message = "VALIDATION.FOOD.SERVING_SIZE.POSITIVE")
    private Double servingSize;

    private ServingUnit servingUnit;

    @PositiveOrZero(message = "VALIDATION.FOOD.CALORIES.POSITIVE")
    private Integer caloriesPerServing;

    private MacroNutrientsDto macrosPerServing;

    @Size(max = 50, message = "VALIDATION.FOOD.BARCODE.SIZE")
    private String barcode;
}