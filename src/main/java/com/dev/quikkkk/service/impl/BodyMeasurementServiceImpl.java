package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.BodyMeasurementMapper;
import com.dev.quikkkk.repository.IBodyMeasurementRepository;
import com.dev.quikkkk.service.IBodyMeasurementService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.dev.quikkkk.enums.ErrorCode.BODY_MEASUREMENT_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.FORBIDDEN_ACCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodyMeasurementServiceImpl implements IBodyMeasurementService {
    private final IBodyMeasurementRepository bodyMeasurementRepository;
    private final BodyMeasurementMapper bodyMeasurementMapper;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    @Transactional
    public BodyMeasurementResponse createBodyMeasurement(CreateBodyMeasurementRequest request) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        BodyMeasurement bodyMeasurement = bodyMeasurementMapper.toEntity(request, client);

        if (bodyMeasurement.getBmi() == null && client.getHeight() != null && client.getHeight() > 0) {
            double heightInMeters = client.getHeight() / 100.0;
            double bmi = bodyMeasurement.getWeight() / (heightInMeters * heightInMeters);
            bodyMeasurement.setBmi(Math.round(bmi * 100.0) / 100.0);
        }

        BodyMeasurement savedMeasurement = bodyMeasurementRepository.save(bodyMeasurement);
        Optional<BodyMeasurement> previousMeasurement = bodyMeasurementRepository
                .findFirstByClient_IdAndMeasurementDateBeforeOrderByMeasurementDateDesc(
                        client.getId(),
                        savedMeasurement.getMeasurementDate()
                );

        BodyMeasurementResponse response = bodyMeasurementMapper.toResponse(savedMeasurement);

        if (previousMeasurement.isPresent()) {
            calculateChanges(response, savedMeasurement, previousMeasurement.get());
        } else {
            response.setWeightChange(0.0);
            response.setBodyFatChange(0.0);
            response.setMuscleMassChange(0.0);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BodyMeasurementResponse> getBodyMeasurements() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public BodyMeasurementResponse getBodyMeasurementById(String id) {
        BodyMeasurement bodyMeasurement = bodyMeasurementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BODY_MEASUREMENT_NOT_FOUND));
        validateAccess(bodyMeasurement);

        return bodyMeasurementMapper.toResponse(bodyMeasurement);
    }

    private void calculateChanges(BodyMeasurementResponse response, BodyMeasurement current, BodyMeasurement previous) {
        if (current.getWeight() != null && previous.getWeight() != null)
            response.setWeightChange(current.getWeight() - previous.getWeight());

        if (current.getBodyFatPercentage() != null && previous.getBodyFatPercentage() != null)
            response.setBodyFatChange(current.getBodyFatPercentage() - previous.getBodyFatPercentage());

        if (current.getMuscleMass() != null && previous.getMuscleMass() != null)
            response.setMuscleMassChange(current.getMuscleMass() - previous.getMuscleMass());
    }

    private void validateAccess(BodyMeasurement bodyMeasurement) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        if (!Objects.equals(client.getId(), bodyMeasurement.getClient().getId()))
            throw new BusinessException(FORBIDDEN_ACCESS);
    }
}
