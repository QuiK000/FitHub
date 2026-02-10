package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.BodyMeasurementMapper;
import com.dev.quikkkk.repository.IBodyMeasurementRepository;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.service.IBodyMeasurementService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodyMeasurementServiceImpl implements IBodyMeasurementService {
    private final IBodyMeasurementRepository bodyMeasurementRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final BodyMeasurementMapper bodyMeasurementMapper;

    @Override
    @Transactional
    public BodyMeasurementResponse createBodyMeasurement(CreateBodyMeasurementRequest request) {
        ClientProfile client = getCurrentClientProfile();
        BodyMeasurement bodyMeasurement = bodyMeasurementMapper.toEntity(request, client);

        if (bodyMeasurement.getBmi() == null && client.getHeight() != null && client.getHeight() > 0) {
            double heightInMeters = client.getHeight() / 100.0;
            double bmi = bodyMeasurement.getWeight() / (heightInMeters * heightInMeters);
            bodyMeasurement.setBmi(Math.round(bmi * 100.0) / 100.0);
        }

        bodyMeasurementRepository.save(bodyMeasurement);
        return bodyMeasurementMapper.toResponse(bodyMeasurement);
    }

    private ClientProfile getCurrentClientProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }
}
