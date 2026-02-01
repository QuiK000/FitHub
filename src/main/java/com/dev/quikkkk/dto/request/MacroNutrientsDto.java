package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
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
public class MacroNutrientsDto {
    @PositiveOrZero(message = "VALIDATION.MACROS.PROTEIN.POSITIVE")
    private Double protein;

    @PositiveOrZero(message = "VALIDATION.MACROS.CARBS.POSITIVE")
    private Double carbs;

    @PositiveOrZero(message = "VALIDATION.MACROS.FATS.POSITIVE")
    private Double fats;

    @PositiveOrZero(message = "VALIDATION.MACROS.FIBER.POSITIVE")
    private Double fiber;

    @PositiveOrZero(message = "VALIDATION.MACROS.SUGAR.POSITIVE")
    private Double sugar;
}
