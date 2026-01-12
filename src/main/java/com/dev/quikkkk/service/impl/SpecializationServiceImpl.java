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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.SPECIALIZATION_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.SPECIALIZATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecializationServiceImpl implements ISpecializationService {
    private final ISpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;

    @Override
    @CacheEvict(value = "specializations", allEntries = true)
    public SpecializationResponse create(CreateSpecializationRequest request) {
        log.info("Specialization request: {}", request);
        if (specializationRepository.existsByNameIgnoreCase(request.getName()))
            throw new BusinessException(SPECIALIZATION_ALREADY_EXISTS);

        Specialization specialization = specializationMapper.toEntity(request);
        specializationRepository.save(specialization);

        return specializationMapper.toResponse(specialization);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "lists",
            key = "'specializations:' + #page + ':' + #size + ':' + (#search != null ? #search : 'all')"
    )
    public PageResponse<SpecializationResponse> getAllActive(int page, int size, String search) {
        log.info("Fetching specializations page, size, search: {}, {}, {}", page, size, search);
        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Specialization> pageResult = specializationRepository.findActiveWithOptionalSearch(search, pageable);

        return PaginationUtils.toPageResponse(pageResult, specializationMapper::toResponse);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "specializations", allEntries = true),
            @CacheEvict(value = "lists", allEntries = true)
    })
    @Transactional
    public SpecializationResponse update(String id, UpdateSpecializationRequest request) {
        log.info("Updating specialization with id: {}", id);
        Specialization specialization = findSpecializationById(id);

        specializationMapper.update(specialization, request);
        specializationRepository.save(specialization);

        return specializationMapper.toResponse(specialization);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "specializations", allEntries = true),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public SpecializationResponse disable(String id) {
        log.info("Specialization with id {} disabled", id);
        Specialization specialization = findSpecializationById(id);

        specialization.setActive(false);
        specializationRepository.save(specialization);

        return specializationMapper.toResponse(specialization);
    }

    private Specialization findSpecializationById(String id) {
        return specializationRepository.findById(id).orElseThrow(() -> new BusinessException(SPECIALIZATION_NOT_FOUND));
    }
}
