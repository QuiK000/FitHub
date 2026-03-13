package com.dev.quikkkk.modules.nutrition.service;

import com.dev.quikkkk.modules.nutrition.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.DailyWaterIntakeResponse;
import com.dev.quikkkk.modules.nutrition.dto.response.WaterIntakeResponse;

import java.util.List;

public interface IWaterIntakeService {
    WaterIntakeResponse createWaterIntake(LogWaterIntakeRequest request);

    DailyWaterIntakeResponse getTodayWaterIntake();

    List<DailyWaterIntakeResponse> getWeeklyWaterIntake();
}
