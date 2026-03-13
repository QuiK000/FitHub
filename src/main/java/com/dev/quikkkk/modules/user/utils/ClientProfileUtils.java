package com.dev.quikkkk.modules.user.utils;

import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.user.repository.IClientProfileRepository;
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
