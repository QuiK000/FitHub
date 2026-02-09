package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.dto.response.WaterIntakeResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.WaterIntake;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.WaterIntakeMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IWaterIntakeRepository;
import com.dev.quikkkk.service.IWaterIntakeService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.dev.quikkkk.enums.ErrorCode.USER_NOT_FOUND;

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
        ClientProfile client = clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        Integer dailyTarget = client.getDailyWaterTarget();

        if (dailyTarget == null) {
            if (client.getWeight() > 0) {
                dailyTarget = (int) (client.getWeight() * 35);
            } else {
                dailyTarget = 2500;
            }
        }

        WaterIntake intake = waterIntakeMapper.toEntity(request, client, dailyTarget, userId);
        waterIntakeRepository.save(intake);

        List<WaterIntake> todayIntakes = waterIntakeRepository.findALlByClientIdAndIntakeDate(
                client.getId(),
                LocalDate.now()
        );

        int totalConsumedToday = todayIntakes.stream()
                .mapToInt(WaterIntake::getAmountMl)
                .sum();

        double progress = (double) totalConsumedToday / dailyTarget * 100;
        return waterIntakeMapper.toResponse(intake, progress);
    }
}
