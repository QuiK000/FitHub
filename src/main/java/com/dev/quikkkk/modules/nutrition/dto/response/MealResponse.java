package com.dev.quikkkk.modules.nutrition.dto.response;

import com.dev.quikkkk.modules.nutrition.dto.request.MacroNutrientsDto;
import com.dev.quikkkk.modules.nutrition.enums.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class MealResponse {
    private String id;
    private MealType mealType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime mealTime;
    private String name;
    private String description;
    private Integer calories;
    private MacroNutrientsDto macros;
    private List<MealFoodResponse> foods;
    private boolean completed;
}
