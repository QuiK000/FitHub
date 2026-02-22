package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateProgressPhotoRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.ProgressPhotoResponse;
import com.dev.quikkkk.entity.BodyMeasurement;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.ProgressPhoto;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.ProgressPhotoMapper;
import com.dev.quikkkk.repository.IBodyMeasurementRepository;
import com.dev.quikkkk.repository.IProgressPhotoRepository;
import com.dev.quikkkk.service.IProgressPhotoService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import com.dev.quikkkk.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.BODY_MEASUREMENT_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.enums.ErrorCode.PROGRESS_PHOTO_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhotoProgressServiceImpl implements IProgressPhotoService {
    private final IProgressPhotoRepository photoRepository;
    private final IBodyMeasurementRepository bodyMeasurementRepository;
    private final ProgressPhotoMapper progressPhotoMapper;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    public ProgressPhotoResponse createPhotoProgress(CreateProgressPhotoRequest request) {
        BodyMeasurement measurement = bodyMeasurementRepository.findById(request.getMeasurementId())
                .orElseThrow(() -> new BusinessException(BODY_MEASUREMENT_NOT_FOUND));

        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        if (!measurement.getClient().getId().equals(client.getId())) throw new BusinessException(FORBIDDEN_ACCESS);

        ProgressPhoto progressPhoto = progressPhotoMapper.toEntity(request, client, measurement);
        photoRepository.save(progressPhoto);

        return progressPhotoMapper.toResponse(progressPhoto);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProgressPhotoResponse> getProgressPhotos(int page, int size) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "photoDate");
        Page<ProgressPhoto> progressPhotoPage = photoRepository.findProgressPhotosByClientId(client.getId(), pageable);

        return PaginationUtils.toPageResponse(progressPhotoPage, progressPhotoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressPhotoResponse getProgressPhotoById(String id) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        ProgressPhoto progressPhoto = photoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PROGRESS_PHOTO_NOT_FOUND));
        if (!progressPhoto.getClient().getId().equals(client.getId())) throw new BusinessException(FORBIDDEN_ACCESS);

        return progressPhotoMapper.toResponse(progressPhoto);
    }
}
