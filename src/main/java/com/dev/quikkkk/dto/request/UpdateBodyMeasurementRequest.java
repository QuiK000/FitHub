package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.MeasurementType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpdateBodyMeasurementRequest {
    @Positive(message = "VALIDATION.MEASUREMENT.WEIGHT.POSITIVE")
    private Double weight;

    @Min(value = 0, message = "VALIDATION.MEASUREMENT.BODY_FAT.MIN")
    @Max(value = 100, message = "VALIDATION.MEASUREMENT.BODY_FAT.MAX")
    private Double bodyFatPercentage;

    @Positive(message = "VALIDATION.MEASUREMENT.MUSCLE_MASS.POSITIVE")
    private Double muscleMass;

    @Positive(message = "VALIDATION.MEASUREMENT.BMI.POSITIVE")
    private Double bmi;

    @Positive(message = "VALIDATION.MEASUREMENT.BMR.POSITIVE")
    private Integer bmr;

    @Min(value = 0, message = "VALIDATION.MEASUREMENT.WATER.MIN")
    @Max(value = 100, message = "VALIDATION.MEASUREMENT.WATER.MAX")
    private Double bodyWaterPercentage;

    @Positive(message = "VALIDATION.MEASUREMENT.BONE_MASS.POSITIVE")
    private Double boneMass;

    @Min(value = 1, message = "VALIDATION.MEASUREMENT.VISCERAL_FAT.MIN")
    @Max(value = 60, message = "VALIDATION.MEASUREMENT.VISCERAL_FAT.MAX")
    private Integer visceralFatLevel;

    private Map<MeasurementType, Double> measurements;

    private String notes;

    private String photoUrl;
}
