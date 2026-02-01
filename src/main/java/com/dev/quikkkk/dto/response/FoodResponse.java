package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.dto.request.MacroNutrientsDto;
import com.dev.quikkkk.enums.ServingUnit;
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
public class FoodResponse {
    private String id;
    private String name;
    private String brand;
    private Double servingSize;
    private ServingUnit servingUnit;
    private Integer caloriesPerServing;
    private MacroNutrientsDto macrosPerServing;
    private String barcode;
    private boolean active;
}
