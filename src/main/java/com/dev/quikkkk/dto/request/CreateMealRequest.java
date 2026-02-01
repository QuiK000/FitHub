package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMealRequest {
    @NotNull(message = "VALIDATION.MEAL.TYPE.NOT_NULl")
    private MealType mealType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime mealTime;

    @Size(max = 200, message = "VALIDATION.MEAL.NAME.SIZE")
    private String name;
    private String description;

    @NotEmpty(message = "VALIDATION.MEAL.FOODS.NOT_EMPTY")
    private List<MealFoodRequest> foods;
}
