package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateSpecializationRequest;
import com.dev.quikkkk.dto.request.UpdateSpecializationRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.SpecializationResponse;

public interface ISpecializationService {
    SpecializationResponse create(CreateSpecializationRequest request);

    PageResponse<SpecializationResponse> getAllActive(int page, int size, String search);

    SpecializationResponse update(String id, UpdateSpecializationRequest request);

    SpecializationResponse disable(String id);
}
