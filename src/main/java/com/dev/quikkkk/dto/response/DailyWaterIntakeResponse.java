package com.dev.quikkkk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyWaterIntakeResponse {
    private LocalDate date;
    private Integer totalMl;
    private Integer targetMl;
    private Double progress;
    private List<WaterIntakeResponse> intakes;
}
