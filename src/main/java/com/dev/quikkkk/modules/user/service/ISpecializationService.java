package com.dev.quikkkk.modules.user.service;

import com.dev.quikkkk.modules.user.dto.request.CreateSpecializationRequest;
import com.dev.quikkkk.modules.user.dto.request.UpdateSpecializationRequest;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.user.dto.response.SpecializationResponse;

public interface ISpecializationService {
    SpecializationResponse create(CreateSpecializationRequest request);

    PageResponse<SpecializationResponse> getAllActive(int page, int size, String search);

    SpecializationResponse update(String id, UpdateSpecializationRequest request);

    SpecializationResponse disable(String id);
}
