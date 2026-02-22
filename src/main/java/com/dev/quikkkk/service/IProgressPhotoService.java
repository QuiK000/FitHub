package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateProgressPhotoRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.ProgressPhotoResponse;

public interface IProgressPhotoService {
    ProgressPhotoResponse createPhotoProgress(CreateProgressPhotoRequest request);

    PageResponse<ProgressPhotoResponse> getProgressPhotos(int page, int size);

    ProgressPhotoResponse getProgressPhotoById(String id);
}
