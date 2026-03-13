package com.dev.quikkkk.modules.progress.service;

import com.dev.quikkkk.modules.progress.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.modules.progress.dto.request.UpdateBodyMeasurementRequest;
import com.dev.quikkkk.modules.progress.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.modules.progress.dto.response.MeasurementHistoryResponse;
import com.dev.quikkkk.core.dto.PageResponse;

public interface IBodyMeasurementService {
    BodyMeasurementResponse createBodyMeasurement(CreateBodyMeasurementRequest request);

    PageResponse<BodyMeasurementResponse> getBodyMeasurements(int page, int size);

    BodyMeasurementResponse getBodyMeasurementById(String id);

    BodyMeasurementResponse updateBodyMeasurement(UpdateBodyMeasurementRequest request, String id);

    BodyMeasurementResponse getLatestBodyMeasurement();

    MeasurementHistoryResponse getHistoryBodyMeasurement();
}
