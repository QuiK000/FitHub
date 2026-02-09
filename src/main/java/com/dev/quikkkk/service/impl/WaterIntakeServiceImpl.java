package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.dto.response.WaterIntakeResponse;
import com.dev.quikkkk.mapper.WaterIntakeMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IWaterIntakeRepository;
import com.dev.quikkkk.service.IWaterIntakeService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaterIntakeServiceImpl implements IWaterIntakeService {
    private final IWaterIntakeRepository waterIntakeRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final WaterIntakeMapper waterIntakeMapper;

    @Override
    @Transactional
    public WaterIntakeResponse createWaterIntake(LogWaterIntakeRequest request) {
        String userId = SecurityUtils.getCurrentUserId();

        return null;
    }
}
