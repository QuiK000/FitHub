package com.dev.quikkkk.utils;

import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.repository.IClientProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientProfileUtils {
    private final IClientProfileRepository clientProfileRepository;

    public ClientProfile getCurrentClientProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_PROFILE_NOT_FOUND));
    }
}
