package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.request.UpdateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
public class BodyMeasurementMapper {
    public BodyMeasurement toEntity(CreateBodyMeasurementRequest request, ClientProfile client) {
        return BodyMeasurement.builder()
                .client(client)
                .createdBy(client.getId())
                .measurementDate(Optional.ofNullable(request.getMeasurementDate()).orElse(LocalDateTime.now()))
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
                .measurements(request.getMeasurements() != null ? request.getMeasurements() : new HashMap<>())
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
                .weightChange(0.0)
                .bodyFatChange(0.0)
                .muscleMassChange(0.0)
                .build();
    }

    public void update(UpdateBodyMeasurementRequest request, BodyMeasurement entity) {
        if (request.getWeight() != null) entity.setWeight(request.getWeight());
        if (request.getBodyFatPercentage() != null) entity.setBodyFatPercentage(request.getBodyFatPercentage());
        if (request.getMuscleMass() != null) entity.setMuscleMass(request.getMuscleMass());
        if (request.getBmi() != null) entity.setBmi(request.getBmi());
        if (request.getBmr() != null) entity.setBmr(request.getBmr());
        if (request.getBodyWaterPercentage() != null) entity.setBodyWaterPercentage(request.getBodyWaterPercentage());
        if (request.getBoneMass() != null) entity.setBoneMass(request.getBoneMass());
        if (request.getVisceralFatLevel() != null) entity.setVisceralFatLevel(request.getVisceralFatLevel());
        if (request.getMeasurements() != null) entity.setMeasurements(request.getMeasurements());
        if (request.getNotes() != null) entity.setNotes(request.getNotes());
        if (request.getPhotoUrl() != null) entity.setPhotoUrl(request.getPhotoUrl());

        entity.setLastModifiedBy(entity.getClient().getId());
    }
}
