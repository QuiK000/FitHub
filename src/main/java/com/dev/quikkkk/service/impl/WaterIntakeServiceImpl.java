package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.dto.response.DailyWaterIntakeResponse;
import com.dev.quikkkk.dto.response.WaterIntakeResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.WaterIntake;
import com.dev.quikkkk.mapper.WaterIntakeMapper;
import com.dev.quikkkk.repository.IWaterIntakeRepository;
import com.dev.quikkkk.service.IWaterIntakeService;
import com.dev.quikkkk.utils.ClientProfileUtils;
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
        int currentTotal = intakes.stream().mapToInt(WaterIntake::getAmountMl).sum();
        int target = client.getDailyWaterTarget() != null ? client.getDailyWaterTarget() : 2500;
        double progress = (target > 0) ? ((double) currentTotal / target) * 100 : 0;

        List<WaterIntakeResponse> intakeDto = intakes.stream()
                .map(intake -> waterIntakeMapper.toResponse(intake, null)) // TODO
                .toList();

        return waterIntakeMapper.toResponse(date, currentTotal, target, progress, intakeDto);
    }
}
