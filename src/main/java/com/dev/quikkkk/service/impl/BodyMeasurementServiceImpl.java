package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.mapper.BodyMeasurementMapper;
import com.dev.quikkkk.repository.IBodyMeasurementRepository;
import com.dev.quikkkk.service.IBodyMeasurementService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        bodyMeasurementRepository.save(bodyMeasurement);
        return bodyMeasurementMapper.toResponse(bodyMeasurement);
    }
}
