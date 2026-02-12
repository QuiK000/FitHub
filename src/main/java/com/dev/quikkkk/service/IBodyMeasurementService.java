package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.dto.response.PageResponse;

public interface IBodyMeasurementService {
    BodyMeasurementResponse createBodyMeasurement(CreateBodyMeasurementRequest request);

    PageResponse<BodyMeasurementResponse> getBodyMeasurements(int page, int size);

    BodyMeasurementResponse getBodyMeasurementById(String id);
}
