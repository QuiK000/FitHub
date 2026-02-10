package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;

public interface IBodyMeasurementService {
    BodyMeasurementResponse createBodyMeasurement(CreateBodyMeasurementRequest request);
}
