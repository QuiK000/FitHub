package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.enums.ClientGender;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.BodyMeasurementMapper;
import com.dev.quikkkk.repository.IBodyMeasurementRepository;
import com.dev.quikkkk.service.IBodyMeasurementService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
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

        if (bodyMeasurement.getBmi() == null && client.getHeight() != null && client.getHeight() > 0)
            calculateAndSetBmi(bodyMeasurement, client.getHeight());

        if (bodyMeasurement.getBmr() == null && client.getBirthdate() != null && client.getGender() != null)
            calculateAndSetBmr(bodyMeasurement, client);

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
            initializeZeroChanges(response);
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

    private void calculateAndSetBmi(BodyMeasurement measurement, Double heightCm) {
        double heightInMeters = heightCm / 100.0;
        double bmi = measurement.getWeight() / (heightInMeters * heightInMeters);
        measurement.setBmi(round(bmi));
    }

    private void calculateAndSetBmr(BodyMeasurement measurement, ClientProfile client) {
        int age = Period.between(client.getBirthdate(), LocalDate.now()).getYears();

        double weight = measurement.getWeight();
        double height = client.getHeight();
        double bmr;

        if (client.getGender() == ClientGender.MALE) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        measurement.setBmr((int) Math.round(bmr));
    }

    private void calculateChanges(BodyMeasurementResponse response, BodyMeasurement current, BodyMeasurement previous) {
        if (current.getWeight() != null && previous.getWeight() != null)
            response.setWeightChange(round(current.getWeight() - previous.getWeight()));

        if (current.getBodyFatPercentage() != null && previous.getBodyFatPercentage() != null)
            response.setBodyFatChange(round(current.getBodyFatPercentage() - previous.getBodyFatPercentage()));

        if (current.getMuscleMass() != null && previous.getMuscleMass() != null)
            response.setMuscleMassChange(round(current.getMuscleMass() - previous.getMuscleMass()));
    }

    private void validateAccess(BodyMeasurement bodyMeasurement) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        if (!Objects.equals(client.getId(), bodyMeasurement.getClient().getId()))
            throw new BusinessException(FORBIDDEN_ACCESS);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void initializeZeroChanges(BodyMeasurementResponse response) {
        response.setWeightChange(0.0);
        response.setBodyFatChange(0.0);
        response.setMuscleMassChange(0.0);
    }
}
