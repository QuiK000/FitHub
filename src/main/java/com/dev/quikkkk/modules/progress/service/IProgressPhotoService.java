package com.dev.quikkkk.modules.progress.service;

import com.dev.quikkkk.modules.progress.dto.request.CreateProgressPhotoRequest;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.progress.dto.response.ProgressPhotoResponse;

public interface IProgressPhotoService {
    ProgressPhotoResponse createPhotoProgress(CreateProgressPhotoRequest request);

    PageResponse<ProgressPhotoResponse> getProgressPhotos(int page, int size);

    ProgressPhotoResponse getProgressPhotoById(String id);
}
