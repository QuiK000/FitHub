package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.request.UpdateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.BodyMeasurementMapper;
import com.dev.quikkkk.repository.IBodyMeasurementRepository;
import com.dev.quikkkk.service.IBodyMeasurementService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import com.dev.quikkkk.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
        BodyMeasurement measurement = bodyMeasurementMapper.toEntity(request, client);

        measurement.calculateDerivedMetrics(client.getHeight(), client.getBirthdate(), client.getGender());

        BodyMeasurement savedMeasurement = bodyMeasurementRepository.save(measurement);
        BodyMeasurementResponse response = bodyMeasurementMapper.toResponse(savedMeasurement);

        bodyMeasurementRepository.findPreviousMeasurement(client.getId(), savedMeasurement.getMeasurementDate())
                .ifPresent(previous -> calculateChanges(response, savedMeasurement, previous));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BodyMeasurementResponse> getBodyMeasurements(int page, int size) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "measurementDate");
        Page<BodyMeasurement> bodyMeasurementPage = bodyMeasurementRepository.findBodyMeasurementsByClientId(
                client.getId(),
                pageable
        );

        return PaginationUtils.toPageResponse(bodyMeasurementPage, bodyMeasurementMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BodyMeasurementResponse getBodyMeasurementById(String id) {
        BodyMeasurement bodyMeasurement = getEntityByIdAndValidateAccess(id);
        return bodyMeasurementMapper.toResponse(bodyMeasurement);
    }

    @Override
    @Transactional
    public BodyMeasurementResponse updateBodyMeasurement(UpdateBodyMeasurementRequest request, String id) {
        BodyMeasurement measurement = getEntityByIdAndValidateAccess(id);

        bodyMeasurementMapper.update(request, measurement);
        ClientProfile client = measurement.getClient();

        measurement.calculateDerivedMetrics(client.getHeight(), client.getBirthdate(), client.getGender());
        return bodyMeasurementMapper.toResponse(measurement);
    }

    @Override
    @Transactional(readOnly = true)
    public BodyMeasurementResponse getLatestBodyMeasurement() {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        return bodyMeasurementRepository
                .findFirstByClientIdOrderByMeasurementDateDesc(client.getId())
                .map(bodyMeasurementMapper::toResponse)
                .orElseThrow(() -> new BusinessException(BODY_MEASUREMENT_NOT_FOUND));
    }

    private BodyMeasurement getEntityByIdAndValidateAccess(String id) {
        BodyMeasurement measurement = bodyMeasurementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BODY_MEASUREMENT_NOT_FOUND));

        ClientProfile currentClient = clientProfileUtils.getCurrentClientProfile();
        if (!Objects.equals(currentClient.getId(), measurement.getClient().getId()))
            throw new BusinessException(FORBIDDEN_ACCESS);

        return measurement;
    }

    private void calculateChanges(BodyMeasurementResponse response, BodyMeasurement current, BodyMeasurement previous) {
        if (current.getWeight() != null && previous.getWeight() != null)
            response.setWeightChange(round(current.getWeight() - previous.getWeight()));

        if (current.getBodyFatPercentage() != null && previous.getBodyFatPercentage() != null)
            response.setBodyFatChange(round(current.getBodyFatPercentage() - previous.getBodyFatPercentage()));

        if (current.getMuscleMass() != null && previous.getMuscleMass() != null)
            response.setMuscleMassChange(round(current.getMuscleMass() - previous.getMuscleMass()));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
