package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.dto.response.WaterIntakeResponse;

public interface IWaterIntakeService {
    WaterIntakeResponse createWaterIntake(LogWaterIntakeRequest request);
}
