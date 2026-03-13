package com.dev.quikkkk.modules.nutrition.service.impl;

import com.dev.quikkkk.modules.nutrition.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.DailyWaterIntakeResponse;
import com.dev.quikkkk.modules.nutrition.dto.response.WaterIntakeResponse;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.nutrition.entity.WaterIntake;
import com.dev.quikkkk.modules.nutrition.mapper.WaterIntakeMapper;
import com.dev.quikkkk.modules.nutrition.repository.IWaterIntakeRepository;
import com.dev.quikkkk.modules.nutrition.service.IWaterIntakeService;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class WaterIntakeServiceImpl implements IWaterIntakeService {
    private final IWaterIntakeRepository waterIntakeRepository;
    private final WaterIntakeMapper waterIntakeMapper;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    @Transactional
    public WaterIntakeResponse createWaterIntake(LogWaterIntakeRequest request) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        int dailyTarget = client.resolveDailyWaterTarget();

        WaterIntake intake = waterIntakeMapper.toEntity(request, client, dailyTarget, client.getUser().getId());
        waterIntakeRepository.save(intake);

        int totalConsumedToday = Optional.ofNullable(
                waterIntakeRepository.sumAmountByClientIdAndIntakeDate(
                        client.getId(),
                        LocalDate.now()
                )
        ).orElse(0);

        double progress = (double) totalConsumedToday / dailyTarget * 100;
        return waterIntakeMapper.toResponse(intake, progress);
    }

    @Override
    @Transactional(readOnly = true)
    public DailyWaterIntakeResponse getTodayWaterIntake() {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        LocalDate today = LocalDate.now();

        List<WaterIntake> intakes = waterIntakeRepository.findAllByClientIdAndIntakeDateBetweenOrderByIntakeTimeAsc(
                client.getId(),
                today,
                today
        );

        return buildDailyResponse(intakes, today, client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyWaterIntakeResponse> getWeeklyWaterIntake() {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);

        List<WaterIntake> weeksIntakes = waterIntakeRepository.findAllByClientIdAndIntakeDateBetweenOrderByIntakeTimeAsc(
                client.getId(),
                start,
                end
        );

        Map<LocalDate, List<WaterIntake>> groupedByDate = weeksIntakes.stream()
                .collect(Collectors.groupingBy(WaterIntake::getIntakeDate));

        List<DailyWaterIntakeResponse> weeklyResponse = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            List<WaterIntake> dailyIntakes = groupedByDate.getOrDefault(date, Collections.emptyList());
            weeklyResponse.add(buildDailyResponse(dailyIntakes, date, client));
        }


        return weeklyResponse;
    }

    private DailyWaterIntakeResponse buildDailyResponse(
            List<WaterIntake> intakes,
            LocalDate date,
            ClientProfile client
    ) {
        int target = client.getDailyWaterTarget() != null ? client.getDailyWaterTarget() : 2500;
        List<WaterIntakeResponse> intakeDto = new ArrayList<>();
        int runningTotal = 0;

        for (WaterIntake intake : intakes) {
            runningTotal += intake.getAmountMl();
            double runningProgress = (target > 0) ? ((double) runningTotal / target) * 100 : 0;

            intakeDto.add(waterIntakeMapper.toResponse(intake, runningProgress));
        }

        double overallProgress = (target > 0) ? ((double) runningTotal / target) * 100 : 0;
        return waterIntakeMapper.toResponse(date, runningTotal, target, overallProgress, intakeDto);
    }
}
