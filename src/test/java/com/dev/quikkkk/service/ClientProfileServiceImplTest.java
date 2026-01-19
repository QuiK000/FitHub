package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.dto.request.UpdateClientProfileRequest;
import com.dev.quikkkk.dto.response.ClientProfileResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.fixtures.TestFixtures;
import com.dev.quikkkk.mapper.ClientProfileMapper;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.service.impl.ClientProfileServiceImpl;
import com.dev.quikkkk.utils.SecurityUtils;
import com.dev.quikkkk.utils.ServiceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_DEACTIVATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientProfileService Tests")
class ClientProfileServiceImplTest {

    @Mock
    private IClientProfileRepository clientProfileRepository;
    @Mock
    private ClientProfileMapper clientProfileMapper;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private ServiceUtils serviceUtils;

    @InjectMocks
    private ClientProfileServiceImpl clientProfileService;

    @Test
    @DisplayName("Should create client profile successfully")
    void createClientProfile_WithValidData_CreatesProfile() {
        User user = TestFixtures.createClientUser();
        CreateClientProfileRequest request = TestFixtures.createClientProfileRequest();
        ClientProfile profile = TestFixtures.createClientProfile(user);

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(user.getId());

            when(serviceUtils.getUserByIdOrThrow(user.getId())).thenReturn(user);
            when(clientProfileMapper.toEntity(request, user)).thenReturn(profile);
            when(clientProfileRepository.save(profile)).thenReturn(profile);
            when(clientProfileMapper.toResponse(profile))
                    .thenReturn(ClientProfileResponse.builder()
                            .firstname(request.getFirstname())
                            .lastname(request.getLastname())
                            .build());

            ClientProfileResponse response =
                    clientProfileService.createClientProfile(request);

            assertThat(response).isNotNull();
            verify(clientProfileRepository).save(profile);
        }
    }

    @Test
    @DisplayName("Should throw exception when profile already exists")
    void createClientProfile_WhenProfileExists_ThrowsBusinessException() {
        User user = TestFixtures.createClientUser();
        user.setClientProfile(TestFixtures.createClientProfile(user));

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(user.getId());

            when(serviceUtils.getUserByIdOrThrow(user.getId())).thenReturn(user);

            assertThatThrownBy(() ->
                    clientProfileService.createClientProfile(
                            TestFixtures.createClientProfileRequest()
                    ))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            CLIENT_PROFILE_ALREADY_EXISTS
                    );

            verify(clientProfileRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should update client profile successfully")
    void updateClientProfile_WithValidData_UpdatesProfile() {
        User user = TestFixtures.createClientUser();
        ClientProfile profile = TestFixtures.createClientProfile(user);
        user.setClientProfile(profile);

        UpdateClientProfileRequest request = UpdateClientProfileRequest.builder()
                .firstname("UpdatedName")
                .weight(80.0)
                .build();

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(user.getId());

            when(serviceUtils.getUserByIdOrThrow(user.getId())).thenReturn(user);
            when(clientProfileRepository.save(profile)).thenReturn(profile);
            when(messageMapper.message("Client profile updated"))
                    .thenReturn(MessageResponse.builder()
                            .message("Client profile updated")
                            .build());

            MessageResponse response =
                    clientProfileService.updateClientProfile(request);

            assertThat(response).isNotNull();
            verify(clientProfileMapper).update(profile, request);
            verify(clientProfileRepository).save(profile);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating deactivated profile")
    void updateClientProfile_WhenProfileDeactivated_ThrowsBusinessException() {
        User user = TestFixtures.createClientUser();
        ClientProfile profile = TestFixtures.createClientProfile(user);
        profile.setActive(false);
        user.setClientProfile(profile);

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(user.getId());

            when(serviceUtils.getUserByIdOrThrow(user.getId())).thenReturn(user);

            assertThatThrownBy(() ->
                    clientProfileService.updateClientProfile(
                            UpdateClientProfileRequest.builder().build()
                    ))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            CLIENT_PROFILE_DEACTIVATED
                    );
        }
    }

    @Test
    @DisplayName("Should get all active clients with pagination")
    void getAllClientsProfile_ReturnsPagedResults() {
        ClientProfile profile = TestFixtures.createClientProfile(
                TestFixtures.createClientUser()
        );

        Page<ClientProfile> page =
                new PageImpl<>(List.of(profile));

        when(clientProfileRepository.findActiveWithOptionalSearch(
                isNull(), any(Pageable.class)
        )).thenReturn(page);

        when(clientProfileMapper.toResponse(any(ClientProfile.class)))
                .thenReturn(ClientProfileResponse.builder()
                        .firstname("John")
                        .lastname("Doe")
                        .build());

        PageResponse<ClientProfileResponse> response =
                clientProfileService.getAllClientsProfile(0, 10, null);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should deactivate profile successfully")
    void deactivateProfile_DeactivatesProfile() {
        User user = TestFixtures.createClientUser();
        ClientProfile profile = TestFixtures.createClientProfile(user);
        user.setClientProfile(profile);

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(user.getId());

            when(serviceUtils.getUserByIdOrThrow(user.getId())).thenReturn(user);
            when(clientProfileRepository.save(profile)).thenReturn(profile);
            when(messageMapper.message("Client profile deactivated"))
                    .thenReturn(MessageResponse.builder()
                            .message("Client profile deactivated")
                            .build());

            MessageResponse response =
                    clientProfileService.deactivateProfile();

            assertThat(response).isNotNull();
            assertThat(profile.isActive()).isFalse();
            verify(clientProfileRepository).save(profile);
        }
    }

    @Test
    @DisplayName("Should clear profile data successfully")
    void clearProfile_ClearsPersonalData() {
        User user = TestFixtures.createClientUser();
        ClientProfile profile = TestFixtures.createClientProfile(user);
        user.setClientProfile(profile);

        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(user.getId());

            when(serviceUtils.getUserByIdOrThrow(user.getId())).thenReturn(user);
            when(clientProfileRepository.save(profile)).thenReturn(profile);
            when(clientProfileMapper.toResponse(profile))
                    .thenReturn(ClientProfileResponse.builder().build());

            ClientProfileResponse response =
                    clientProfileService.clearProfile();

            assertThat(response).isNotNull();
            assertThat(profile.getFirstname()).isNull();
            assertThat(profile.getLastname()).isNull();
            assertThat(profile.getPhone()).isNull();
            assertThat(profile.isActive()).isFalse();
        }
    }
}
