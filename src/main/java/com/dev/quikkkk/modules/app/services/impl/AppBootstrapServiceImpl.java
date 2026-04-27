package com.dev.quikkkk.modules.app.services.impl;

import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.app.dto.enums.ProfileStatus;
import com.dev.quikkkk.modules.app.dto.response.AppBootstrapResponse;
import com.dev.quikkkk.modules.app.services.IAppBootstrapService;
import com.dev.quikkkk.modules.user.dto.response.UserResponse;
import com.dev.quikkkk.modules.user.enums.RoleName;
import com.dev.quikkkk.modules.user.service.IClientProfileService;
import com.dev.quikkkk.modules.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppBootstrapServiceImpl implements IAppBootstrapService {
    private final IUserService userService;
    private final IClientProfileService clientProfileService;

    @Override
    @Transactional(readOnly = true)
    public AppBootstrapResponse getBootstrap() {
        UserResponse user = userService.getCurrentUser();
        Set<String> roles = user.getRoles() == null ? Collections.emptySet() : user.getRoles();

        ProfileStatus profileStatus = resolveProfileStatus(roles);
        boolean onboardingRequired = roles.contains(RoleName.CLIENT.name()) && profileStatus != ProfileStatus.ACTIVE;

        return AppBootstrapResponse.builder()
                .user(user)
                .roles(roles)
                .profileStatus(profileStatus)
                .onboardingRequired(onboardingRequired)
                .permissions(resolvePermissions(roles))
                .build();
    }

    private ProfileStatus resolveProfileStatus(Set<String> roles) {
        if (!roles.contains(RoleName.CLIENT.name())) return ProfileStatus.NOT_APPLICABLE;

        try {
            clientProfileService.getClientProfile();
            return ProfileStatus.ACTIVE;
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == CLIENT_PROFILE_NOT_FOUND) return ProfileStatus.MISSING;
            if (ex.getErrorCode() == CLIENT_PROFILE_DEACTIVATED) return ProfileStatus.INACTIVE;

            throw ex;
        }
    }

    private Set<String> resolvePermissions(Set<String> roles) {
        Set<String> permissions = new LinkedHashSet<>();

        if (roles.contains(RoleName.CLIENT.name())) {
            permissions.add("profile:read");
            permissions.add("profile:write");
            permissions.add("workouts:read");
            permissions.add("workouts:log");
        }

        if (roles.contains(RoleName.TRAINER.name())) {
            permissions.add("sessions:manage");
            permissions.add("workouts:assign");
            permissions.add("clients:read");
        }

        if (roles.contains(RoleName.ADMIN.name())) {
            permissions.add("admin:read");
            permissions.add("admin:write");
            permissions.add("users:manage");
        }

        return permissions;
    }
}
