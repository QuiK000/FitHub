package com.dev.quikkkk.service;

import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.user.dto.response.TrainerProfileResponse;
import com.dev.quikkkk.modules.user.entity.Specialization;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.mapper.TrainerProfileMapper;
import com.dev.quikkkk.modules.user.repository.ISpecializationRepository;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
import com.dev.quikkkk.modules.user.service.impl.TrainerProfileServiceImpl;
import com.dev.quikkkk.core.utils.ServiceUtils;
import com.dev.quikkkk.modules.user.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.SPECIALIZATION_NOT_FOUND_OR_INACTIVE;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_ALREADY_EXISTS;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerProfileService Tests")
class TrainerProfileServiceImplTest {

    @Mock
    private ITrainerProfileRepository trainerProfileRepository;
    @Mock
    private ISpecializationRepository specializationRepository;
    @Mock
    private com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository trainingSessionRepository;
    @Mock
    private TrainerProfileMapper trainerProfileMapper;
    @Mock
    private com.dev.quikkkk.core.mapper.MessageMapper messageMapper;
    @Mock
    private ServiceUtils serviceUtils;

    @InjectMocks
    private TrainerProfileServiceImpl trainerProfileService;

    @Test
    @DisplayName("Should create trainer profile successfully")
    void createTrainerProfile_WithValidRequest_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            User user = User.builder().id("user-id").trainerProfile(null).build();
            Specialization spec = Specialization.builder().id(UUID.randomUUID().toString()).active(true).build();
            CreateTrainerProfileRequest request = CreateTrainerProfileRequest.builder()
                    .firstname("Jane")
                    .lastname("Doe")
                    .specializationIds(Set.of(spec.getId()))
                    .experienceYears(5)
                    .description("Expert trainer")
                    .build();
            TrainerProfile profile = TrainerProfile.builder().id(UUID.randomUUID().toString()).build();
            TrainerProfileResponse expected = TrainerProfileResponse.builder().id(profile.getId()).build();

            when(serviceUtils.getUserByIdOrThrow("user-id")).thenReturn(user);
            when(specializationRepository.findByIdInAndActiveTrue(Set.of(spec.getId()))).thenReturn(Set.of(spec));
            when(trainerProfileMapper.toEntity(request, user, Set.of(spec))).thenReturn(profile);
            when(trainerProfileMapper.toResponse(profile)).thenReturn(expected);

            TrainerProfileResponse response = trainerProfileService.createTrainerProfile(request);

            assertThat(response).isNotNull();
            verify(trainerProfileRepository).save(profile);
        }
    }

    @Test
    @DisplayName("Should throw exception when trainer profile already exists")
    void createTrainerProfile_WhenProfileExists_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            User user = User.builder().id("user-id")
                    .trainerProfile(TrainerProfile.builder().id(UUID.randomUUID().toString()).build())
                    .build();

            when(serviceUtils.getUserByIdOrThrow("user-id")).thenReturn(user);

            assertThatThrownBy(() -> trainerProfileService.createTrainerProfile(
                    CreateTrainerProfileRequest.builder().build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", TRAINER_PROFILE_ALREADY_EXISTS);
        }
    }

    @Test
    @DisplayName("Should throw exception when specialization not found")
    void createTrainerProfile_WithInvalidSpecialization_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            User user = User.builder().id("user-id").trainerProfile(null).build();
            CreateTrainerProfileRequest request = CreateTrainerProfileRequest.builder()
                    .specializationIds(Set.of("spec-1", "spec-2"))
                    .build();

            when(serviceUtils.getUserByIdOrThrow("user-id")).thenReturn(user);
            when(specializationRepository.findByIdInAndActiveTrue(Set.of("spec-1", "spec-2")))
                    .thenReturn(Set.of(Specialization.builder().id("spec-1").active(true).build()));

            assertThatThrownBy(() -> trainerProfileService.createTrainerProfile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", SPECIALIZATION_NOT_FOUND_OR_INACTIVE);
        }
    }

    @Test
    @DisplayName("Should get trainer profile")
    void getTrainerProfile_WithValidUser_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            User user = User.builder().id("user-id").build();
            TrainerProfile profile = TrainerProfile.builder().id(UUID.randomUUID().toString()).active(true).build();
            TrainerProfileResponse expected = TrainerProfileResponse.builder().id(profile.getId()).build();

            when(serviceUtils.getUserByIdOrThrow("user-id")).thenReturn(user);
            when(trainerProfileRepository.findByUserIdWithSpecializations("user-id")).thenReturn(Optional.of(profile));
            when(trainerProfileMapper.toResponse(profile)).thenReturn(expected);

            TrainerProfileResponse response = trainerProfileService.getTrainerProfile();

            assertThat(response).isNotNull();
        }
    }

    @Test
    @DisplayName("Should throw exception when trainer profile not found")
    void getTrainerProfile_WhenNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            User user = User.builder().id("user-id").build();

            when(serviceUtils.getUserByIdOrThrow("user-id")).thenReturn(user);
            when(trainerProfileRepository.findByUserIdWithSpecializations("user-id")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> trainerProfileService.getTrainerProfile())
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", TRAINER_PROFILE_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should throw exception when trainer profile is deactivated")
    void getTrainerProfile_WhenDeactivated_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            User user = User.builder().id("user-id").build();
            TrainerProfile profile = TrainerProfile.builder().id(UUID.randomUUID().toString()).active(false).build();

            when(serviceUtils.getUserByIdOrThrow("user-id")).thenReturn(user);
            when(trainerProfileRepository.findByUserIdWithSpecializations("user-id")).thenReturn(Optional.of(profile));

            assertThatThrownBy(() -> trainerProfileService.getTrainerProfile())
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", TRAINER_PROFILE_DEACTIVATED);
        }
    }

    @Test
    @DisplayName("Should deactivate trainer profile and cancel future sessions")
    void deactivateProfile_WithActiveProfile_DeactivatesAndCancelsSessions() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            User user = User.builder().id("user-id").build();
            TrainerProfile profile = TrainerProfile.builder().id(UUID.randomUUID().toString()).active(true).build();

            when(serviceUtils.getUserByIdOrThrow("user-id")).thenReturn(user);
            when(trainerProfileRepository.findByUserIdWithSpecializations("user-id")).thenReturn(Optional.of(profile));
            when(messageMapper.message("Trainer profile deactivated, future sessions cancelled"))
                    .thenReturn(com.dev.quikkkk.core.dto.MessageResponse.builder().build());

            var response = trainerProfileService.deactivateProfile();

            assertThat(response).isNotNull();
            assertThat(profile.isActive()).isFalse();
            verify(trainerProfileRepository).save(profile);
        }
    }
}
