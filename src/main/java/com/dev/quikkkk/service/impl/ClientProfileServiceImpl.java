package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.dto.request.UpdateClientProfileRequest;
import com.dev.quikkkk.dto.response.ClientProfileResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.ClientProfileMapper;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.service.IClientProfileService;
import com.dev.quikkkk.utils.SecurityUtils;
import com.dev.quikkkk.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientProfileServiceImpl implements IClientProfileService {
    private final IClientProfileRepository clientProfileRepository;
    private final ClientProfileMapper clientProfileMapper;
    private final MessageMapper messageMapper;
    private final ServiceUtils serviceUtils;

    @Override
    @Transactional
    public ClientProfileResponse createClientProfile(CreateClientProfileRequest request) {
        User user = getCurrentUser();
        if (user.getClientProfile() != null) throw new BusinessException(CLIENT_PROFILE_ALREADY_EXISTS);

        ClientProfile profile = clientProfileMapper.toEntity(request, user);

        user.setClientProfile(profile);
        clientProfileRepository.save(profile);

        log.info("Client profile created: {}", profile.getId());
        return clientProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientProfileResponse getClientProfile() {
        User user = getCurrentUser();
        ClientProfile profile = clientProfileRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        log.info("Getting client profile for user: {}", user.getId());
        ensureProfileIsActive(profile);
        return clientProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public MessageResponse updateClientProfile(UpdateClientProfileRequest request) {
        User user = getCurrentUser();
        ClientProfile profile = getClientProfileOrThrow(user);

        ensureProfileIsActive(profile);
        clientProfileMapper.update(profile, request);
        clientProfileRepository.save(profile);

        log.info("Client profile updated: {}", profile.getId());
        return messageMapper.message("Client profile updated");
    }

    @Override
    @Transactional
    public MessageResponse deactivateProfile() {
        User user = getCurrentUser();
        ClientProfile profile = getClientProfileOrThrow(user);
        ensureProfileIsActive(profile);

        log.info("Deactivating profile: {}", profile.getId());

        profile.setActive(false);
        clientProfileRepository.save(profile);

        log.info("Client profile deactivated: {}", profile.getId());
        return messageMapper.message("Client profile deactivated");
    }

    @Override
    @Transactional
    public ClientProfileResponse clearProfile() {
        User user = getCurrentUser();
        ClientProfile profile = getClientProfileOrThrow(user);

        ensureProfileIsActive(profile);
        profile.clearPersonalData();
        profile.setActive(false);

        clientProfileRepository.save(profile);
        return clientProfileMapper.toResponse(profile);
    }

    private User getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        return serviceUtils.getUserByIdOrThrow(userId);
    }

    private ClientProfile getClientProfileOrThrow(User user) {
        if (user.getClientProfile() == null) throw new BusinessException(CLIENT_PROFILE_NOT_FOUND);
        return user.getClientProfile();
    }

    private void ensureProfileIsActive(ClientProfile profile) {
        if (!profile.isActive()) throw new BusinessException(CLIENT_PROFILE_DEACTIVATED);
    }
}
