package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateSpecializationRequest;
import com.dev.quikkkk.dto.request.UpdateSpecializationRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.SpecializationResponse;
import com.dev.quikkkk.entity.Specialization;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.SpecializationMapper;
import com.dev.quikkkk.repository.ISpecializationRepository;
import com.dev.quikkkk.service.ISpecializationService;
import com.dev.quikkkk.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.dev.quikkkk.enums.ErrorCode.SPECIALIZATION_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.SPECIALIZATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecializationServiceImpl implements ISpecializationService {
    private final ISpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;

    @Override
    public SpecializationResponse create(CreateSpecializationRequest request) {
        if (specializationRepository.existsByNameIgnoreCase(request.getName()))
            throw new BusinessException(SPECIALIZATION_ALREADY_EXISTS);

        Specialization specialization = specializationMapper.toEntity(request);
        specializationRepository.save(specialization);

        return specializationMapper.toResponse(specialization);
    }

    @Override
    public PageResponse<SpecializationResponse> getAllActive(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Specialization> specializationPage;

        if (search != null && !search.isBlank()) {
            specializationPage = specializationRepository.findAllByActiveTrueAndNameContainingIgnoreCase(
                    search, pageable
            );
        } else {
            specializationPage = specializationRepository.findAllByActiveTrue(pageable);
        }

        return PaginationUtils.toPageResponse(specializationPage, specializationMapper::toResponse);
    }

    @Override
    public SpecializationResponse update(String id, UpdateSpecializationRequest request) {
        Specialization specialization = findSpecializationById(id);

        specializationMapper.update(specialization, request);
        specializationRepository.save(specialization);

        return specializationMapper.toResponse(specialization);
    }

    @Override
    public SpecializationResponse disable(String id) {
        Specialization specialization = findSpecializationById(id);

        specialization.setActive(false);
        specializationRepository.save(specialization);

        return specializationMapper.toResponse(specialization);
    }

    private Specialization findSpecializationById(String id) {
        return specializationRepository.findById(id).orElseThrow(() -> new BusinessException(SPECIALIZATION_NOT_FOUND));
    }
}
