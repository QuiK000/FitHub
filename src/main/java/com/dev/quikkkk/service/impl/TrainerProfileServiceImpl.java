package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.dto.request.UpdateTrainerProfileRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.TrainerProfileResponse;
import com.dev.quikkkk.entity.Specialization;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.TrainerProfileMapper;
import com.dev.quikkkk.repository.ISpecializationRepository;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.service.ITrainerProfileService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import com.dev.quikkkk.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.dev.quikkkk.enums.ErrorCode.SPECIALIZATION_NOT_FOUND_OR_INACTIVE;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerProfileServiceImpl implements ITrainerProfileService {
    private final ITrainerProfileRepository trainerProfileRepository;
    private final ISpecializationRepository specializationRepository;
    private final TrainerProfileMapper trainerProfileMapper;
    private final MessageMapper messageMapper;
    private final ServiceUtils serviceUtils;

    @Override
    @Transactional
    @CacheEvict(value = "trainerProfiles", allEntries = true)
    public TrainerProfileResponse createTrainerProfile(CreateTrainerProfileRequest request) {
        User user = getCurrentUser();
        if (user.getTrainerProfile() != null) throw new BusinessException(TRAINER_PROFILE_ALREADY_EXISTS);

        Set<Specialization> specializations = specializationRepository
                .findByIdInAndActiveTrue(request.getSpecializationIds());
        if (specializations.size() != request.getSpecializationIds().size())
            throw new BusinessException(SPECIALIZATION_NOT_FOUND_OR_INACTIVE);

        TrainerProfile profile = trainerProfileMapper.toEntity(request, user, specializations);

        user.setTrainerProfile(profile);
        trainerProfileRepository.save(profile);

        log.info("Trainer profile created: {}", profile.getId());
        return trainerProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "trainerProfiles",
            key = "'current:' + T(com.dev.quikkkk.utils.SecurityUtils).getCurrentUserId()"
    )
    public TrainerProfileResponse getTrainerProfile() {
        User user = getCurrentUser();
        TrainerProfile profile = getTrainerProfileOrThrow(user);

        log.info("Trainer profile retrieved: {}", profile.getId());
        ensureProfileIsActive(profile);
        return trainerProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "lists",
            key = "'trainers:' + #page + ':' + #size + ':' + (#search != null ? #search : 'all')"
    )
    public PageResponse<TrainerProfileResponse> findAllTrainerProfiles(int page, int size, String search) {
        log.info("Getting all trainer profiles for page: {}, size: {}", page, size);
        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<TrainerProfile> trainerProfilePage = trainerProfileRepository.findActiveWithOptionalSearch(search, pageable);

        return PaginationUtils.toPageResponse(trainerProfilePage, trainerProfileMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "trainerProfiles", key = "'byId:' + #id")
    public TrainerProfileResponse getTrainerById(String id) {
        log.info("Getting trainer profile by id: {}", id);
        return trainerProfileRepository.findTrainerProfileById(id)
                .map(trainerProfileMapper::toResponse)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(
                    value = "trainerProfiles",
                    key = "'current:' + T(com.dev.quikkkk.utils.SecurityUtils).getCurrentUserId()",
                    allEntries = true
            ),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public TrainerProfileResponse updateTrainerProfile(UpdateTrainerProfileRequest request) {
        User user = getCurrentUser();
        TrainerProfile profile = getTrainerProfileOrThrow(user);

        ensureProfileIsActive(profile);
        Set<Specialization> specializations = null;

        if (request.getSpecializationIds() != null) {
            specializations = specializationRepository.findByIdInAndActiveTrue(request.getSpecializationIds());
            if (specializations.size() != request.getSpecializationIds().size()) {
                throw new BusinessException(SPECIALIZATION_NOT_FOUND_OR_INACTIVE);
            }
        }

        log.info("Trainer profile updated: {}", profile.getId());
        trainerProfileMapper.update(profile, request, specializations);
        trainerProfileRepository.save(profile);

        return trainerProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "trainerProfiles", allEntries = true),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public MessageResponse deactivateProfile() {
        User user = getCurrentUser();
        TrainerProfile profile = getTrainerProfileOrThrow(user);
        ensureProfileIsActive(profile);

        log.info("Deactivating profile: {}", profile.getId());

        profile.setActive(false);
        trainerProfileRepository.save(profile);

        log.info("Trainer profile deactivated: {}", profile.getId());
        return messageMapper.message("Trainer profile deactivated");
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "trainerProfiles", allEntries = true),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public TrainerProfileResponse clearProfile() {
        log.info("Clearing trainer profile");
        User user = getCurrentUser();
        TrainerProfile profile = getTrainerProfileOrThrow(user);

        ensureProfileIsActive(profile);
        profile.clearPersonalData();
        profile.setActive(false);

        trainerProfileRepository.save(profile);
        return trainerProfileMapper.toResponse(profile);
    }

    private User getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        return serviceUtils.getUserByIdOrThrow(userId);
    }

    private TrainerProfile getTrainerProfileOrThrow(User user) {
        return trainerProfileRepository.findByUserIdWithSpecializations(user.getId())
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }

    private void ensureProfileIsActive(TrainerProfile profile) {
        if (!profile.isActive()) throw new BusinessException(TRAINER_PROFILE_DEACTIVATED);
    }
}
