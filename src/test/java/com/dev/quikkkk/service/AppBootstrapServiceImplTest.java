package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.app.dto.enums.ProfileStatus;
import com.dev.quikkkk.modules.app.dto.response.AppBootstrapResponse;
import com.dev.quikkkk.modules.app.services.impl.AppBootstrapServiceImpl;
import com.dev.quikkkk.modules.user.dto.response.UserResponse;
import com.dev.quikkkk.modules.user.service.IClientProfileService;
import com.dev.quikkkk.modules.user.service.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppBootstrapService Tests")
public class AppBootstrapServiceImplTest {
    @Mock
    private IUserService userService;

    @Mock
    private IClientProfileService clientProfileService;

    @InjectMocks
    private AppBootstrapServiceImpl appBootstrapService;

    @Test
    @DisplayName("Should return active profile status for client with profile")
    void getBootstrap_WithActiveClientProfile_ReturnsActiveStatus() {
        UserResponse user = UserResponse.builder()
                .id("u-1")
                .email("client@example.com")
                .roles(Set.of("CLIENT"))
                .enabled(true)
                .build();

        when(userService.getCurrentUser()).thenReturn(user);
        AppBootstrapResponse response = appBootstrapService.getBootstrap();

        assertThat(response.getProfileStatus()).isEqualTo(ProfileStatus.ACTIVE);
        assertThat(response.isOnboardingRequired()).isFalse();
        assertThat(response.getPermissions()).contains("profile:read", "workouts:log");

        verify(clientProfileService).getClientProfile();
    }

    @Test
    @DisplayName("Should require onboarding for client without profile")
    void getBoostrap_WithMissingClientProfile_ReturnsMissingStatus() {
        UserResponse user = UserResponse.builder()
                .id("u-2")
                .email("missing@example.com")
                .roles(Set.of("CLIENT"))
                .enabled(true)
                .build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(clientProfileService.getClientProfile()).thenThrow(new BusinessException(ErrorCode.CLIENT_PROFILE_NOT_FOUND));

        AppBootstrapResponse response = appBootstrapService.getBootstrap();

        assertThat(response.getProfileStatus()).isEqualTo(ProfileStatus.MISSING);
        assertThat(response.isOnboardingRequired()).isTrue();
    }

    @Test
    @DisplayName("Should skip profile lookup for non-client role")
    void getBootstrap_WithTrainerRole_SkipsProfileLookup() {
        UserResponse user = UserResponse.builder()
                .id("u-3")
                .email("trainer@example.com")
                .roles(Set.of("TRAINER"))
                .enabled(true)
                .build();

        when(userService.getCurrentUser()).thenReturn(user);
        AppBootstrapResponse response = appBootstrapService.getBootstrap();

        assertThat(response.getProfileStatus()).isEqualTo(ProfileStatus.NOT_APPLICABLE);
        assertThat(response.isOnboardingRequired()).isFalse();
        assertThat(response.getPermissions()).contains("sessions:manage", "workouts:assign");
    }
}
