package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateProgressPhotoRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementShortResponse;
import com.dev.quikkkk.dto.response.ProgressPhotoResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.ProgressPhoto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProgressPhotoMapper {
    public ProgressPhoto toEntity(
            CreateProgressPhotoRequest request,
            ClientProfile client,
            BodyMeasurement measurement
    ) {
        return ProgressPhoto.builder()
                .client(client)
                .measurement(measurement)
                .photoUrl(request.getPhotoUrl())
                .angle(request.getAngle())
                .notes(request.getNotes())
                .photoDate(LocalDateTime.now())
                .createdBy(client.getId())
                .build();
    }

    public ProgressPhotoResponse toResponse(ProgressPhoto progressPhoto) {
        return ProgressPhotoResponse.builder()
                .id(progressPhoto.getId())
                .photoDate(progressPhoto.getPhotoDate())
                .photoUrl(progressPhoto.getPhotoUrl())
                .angle(progressPhoto.getAngle())
                .notes(progressPhoto.getNotes())
                .measurement(BodyMeasurementShortResponse.builder()
                        .id(progressPhoto.getMeasurement().getId())
                        .measurementDate(progressPhoto.getMeasurement().getMeasurementDate())
                        .weight(progressPhoto.getMeasurement().getWeight())
                        .bodyFatPercentage(progressPhoto.getMeasurement().getBodyFatPercentage())
                        .build())
                .build();
    }
}
