package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BodyMeasurementMapper {
    public BodyMeasurement toEntity(CreateBodyMeasurementRequest request, ClientProfile client) {
        return BodyMeasurement.builder()
                .client(client)
                .createdBy(client.getId())
                .measurementDate(LocalDateTime.now())
                .weight(request.getWeight())
                .bodyFatPercentage(request.getBodyFatPercentage())
                .muscleMass(request.getMuscleMass())
                .bmi(request.getBmi())
                .bmr(request.getBmr())
                .bodyWaterPercentage(request.getBodyWaterPercentage())
                .boneMass(request.getBoneMass())
                .visceralFatLevel(request.getVisceralFatLevel())
                .notes(request.getNotes())
                .photoUrl(request.getPhotoUrl())
                .measurements(request.getMeasurements())
                .build();
    }

    public BodyMeasurementResponse toResponse(BodyMeasurement bodyMeasurement) {
        return BodyMeasurementResponse.builder()
                .id(bodyMeasurement.getId())
                .measurementDate(bodyMeasurement.getMeasurementDate())
                .weight(bodyMeasurement.getWeight())
                .bodyFatPercentage(bodyMeasurement.getBodyFatPercentage())
                .muscleMass(bodyMeasurement.getMuscleMass())
                .bmi(bodyMeasurement.getBmi())
                .bmr(bodyMeasurement.getBmr())
                .bodyWaterPercentage(bodyMeasurement.getBodyWaterPercentage())
                .boneMass(bodyMeasurement.getBoneMass())
                .visceralFatLevel(bodyMeasurement.getVisceralFatLevel())
                .measurements(bodyMeasurement.getMeasurements())
                .notes(bodyMeasurement.getNotes())
                .photoUrl(bodyMeasurement.getPhotoUrl())
                .build();
    }
}
