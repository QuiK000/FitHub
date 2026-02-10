package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.MeasurementType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BodyMeasurementResponse {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime measurementDate;
    private Double weight;
    private Double bodyFatPercentage;
    private Double muscleMass;
    private Double bmi;
    private Integer bmr;
    private Double bodyWaterPercentage;
    private Double boneMass;
    private Integer visceralFatLevel;
    private Map<MeasurementType, Double> measurements;
    private String notes;
    private String photoUrl;
    private Double weightChange;
    private Double bodyFatChange;
    private Double muscleMassChange;
}
