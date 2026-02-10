package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.dto.response.DailyWaterIntakeResponse;
import com.dev.quikkkk.dto.response.WaterIntakeResponse;

import java.util.List;

public interface IWaterIntakeService {
    WaterIntakeResponse createWaterIntake(LogWaterIntakeRequest request);

    DailyWaterIntakeResponse getTodayWaterIntake();

    List<DailyWaterIntakeResponse> getWeeklyWaterIntake();
}
