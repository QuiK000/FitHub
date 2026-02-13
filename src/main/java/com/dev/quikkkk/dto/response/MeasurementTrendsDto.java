package com.dev.quikkkk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MeasurementTrendsDto {
    private Double totalWeightChange;
    private Double totalBodyFatChange;
    private Double totalMuscleMassChange;
    private Integer measurementCount;
    private Integer daysSinceFirst;
}
