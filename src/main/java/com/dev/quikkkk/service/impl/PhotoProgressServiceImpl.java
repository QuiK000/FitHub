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
import static com.dev.quikkkk.enums.ErrorCode.PROGRESS_PHOTO_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PhotoProgressServiceImpl implements IProgressPhotoService {
    private final IProgressPhotoRepository photoRepository;
    private final IBodyMeasurementRepository bodyMeasurementRepository;
    private final ProgressPhotoMapper progressPhotoMapper;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    @Transactional
    public ProgressPhotoResponse createPhotoProgress(CreateProgressPhotoRequest request) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        log.info("Creating progress photo for client: {}", client.getId());

        BodyMeasurement measurement = null;
        if (request.getMeasurementId() != null) {
            measurement = bodyMeasurementRepository.findById(request.getMeasurementId())
                    .filter(m -> m.getClient().getId().equals(client.getId()))
                    .orElseThrow(() -> {
                        log.warn("Client {} tried to link measurement {} not belonging to them",
                                client.getId(),
                                request.getMeasurementId());
                        return new BusinessException(BODY_MEASUREMENT_NOT_FOUND);
                    });
        }

        ProgressPhoto progressPhoto = progressPhotoMapper.toEntity(request, client, measurement);
        ProgressPhoto savedPhoto = photoRepository.save(progressPhoto);

        log.info("Progress photo created successfully with ID: {}", savedPhoto.getId());
        return progressPhotoMapper.toResponse(savedPhoto);
    }

    @Override
    public PageResponse<ProgressPhotoResponse> getProgressPhotos(int page, int size) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        log.debug("Fetching progress photos for client: {}, page: {}", client.getId(), page);

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "photoDate");
        Page<ProgressPhoto> progressPhotoPage = photoRepository.findAllByClientId(client.getId(), pageable);

        return PaginationUtils.toPageResponse(progressPhotoPage, progressPhotoMapper::toResponse);
    }

    @Override
    public ProgressPhotoResponse getProgressPhotoById(String id) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        return photoRepository.findByIdAndClientId(id, client.getId())
                .map(progressPhotoMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Progress photo not found or access denied. PhotoID: {}, ClientID: {}", id, client.getId());
                    return new BusinessException(PROGRESS_PHOTO_NOT_FOUND);
                });
    }
}
