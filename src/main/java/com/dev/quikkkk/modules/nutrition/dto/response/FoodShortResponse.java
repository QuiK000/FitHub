package com.dev.quikkkk.modules.nutrition.dto.response;

import com.dev.quikkkk.modules.nutrition.enums.ServingUnit;
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
public class FoodShortResponse {
    private String id;
    private String name;
    private String brand;
    private ServingUnit servingUnit;
}
