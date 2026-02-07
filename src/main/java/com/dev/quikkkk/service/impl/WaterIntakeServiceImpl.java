package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.dto.response.WaterIntakeResponse;
import com.dev.quikkkk.service.IWaterIntakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaterIntakeServiceImpl implements IWaterIntakeService {
    @Override
    @Transactional
    public WaterIntakeResponse createWaterIntake(LogWaterIntakeRequest request) {
        return null;
    }
}
