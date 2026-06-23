package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.workout.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.modules.workout.dto.request.UpdateWorkoutPlanRequest;
import com.dev.quikkkk.modules.workout.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.modules.workout.entity.WorkoutPlan;
import com.dev.quikkkk.modules.workout.enums.DifficultyLevel;
import com.dev.quikkkk.modules.workout.mapper.WorkoutPlanMapper;
import com.dev.quikkkk.modules.workout.repository.IWorkoutPlanRepository;
import com.dev.quikkkk.modules.workout.service.impl.WorkoutPlanServiceImpl;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
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

import static com.dev.quikkkk.core.enums.ErrorCode.WORKOUT_PLAN_ALREADY_ACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.WORKOUT_PLAN_ALREADY_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.WORKOUT_PLAN_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.WORKOUT_PLAN_FORBIDDEN;
import static com.dev.quikkkk.core.enums.ErrorCode.WORKOUT_PLAN_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkoutPlanService Tests")
class WorkoutPlanServiceImplTest {

    @Mock
    private IWorkoutPlanRepository workoutPlanRepository;
    @Mock
    private ITrainerProfileRepository trainerProfileRepository;
    @Mock
    private WorkoutPlanMapper workoutPlanMapper;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private WorkoutPlanServiceImpl workoutPlanService;

    @Test
    @DisplayName("Should create workout plan successfully")
    void createWorkoutPlan_WithValidRequest_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("trainer-user-id");

            TrainerProfile trainer = TrainerProfile.builder().id(UUID.randomUUID().toString()).active(true).build();
            CreateWorkoutPlanRequest request = CreateWorkoutPlanRequest.builder()
                    .name("Push Day")
                    .description("Push day workout")
                    .difficultyLevel(DifficultyLevel.BEGINNER)
                    .build();
            WorkoutPlan plan = WorkoutPlan.builder().id(UUID.randomUUID().toString()).name("Push Day").trainer(trainer).active(true).build();
            WorkoutPlanResponse expected = WorkoutPlanResponse.builder().id(plan.getId()).name("Push Day").build();

            when(trainerProfileRepository.findTrainerProfileByUserId("trainer-user-id")).thenReturn(Optional.of(trainer));
            when(workoutPlanMapper.toEntity(request, trainer)).thenReturn(plan);
            when(workoutPlanMapper.toResponse(plan)).thenReturn(expected);

            WorkoutPlanResponse response = workoutPlanService.createWorkoutPlan(request);

            assertThat(response).isNotNull();
            verify(workoutPlanRepository).save(plan);
        }
    }

    @Test
    @DisplayName("Should throw exception when trainer profile not found")
    void createWorkoutPlan_WhenTrainerNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("unknown-user-id");

            when(trainerProfileRepository.findTrainerProfileByUserId("unknown-user-id")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutPlanService.createWorkoutPlan(
                    CreateWorkoutPlanRequest.builder().build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", TRAINER_PROFILE_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should get workout plan by id")
    void getWorkoutPlanById_WithValidId_ReturnsResponse() {
        String planId = UUID.randomUUID().toString();
        WorkoutPlan plan = WorkoutPlan.builder().id(planId).name("Leg Day").active(true).build();
        WorkoutPlanResponse expected = WorkoutPlanResponse.builder().id(planId).name("Leg Day").build();

        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(workoutPlanMapper.toResponse(plan)).thenReturn(expected);

        WorkoutPlanResponse response = workoutPlanService.getWorkoutPlanById(planId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(planId);
    }

    @Test
    @DisplayName("Should throw exception when workout plan not found")
    void getWorkoutPlanById_WithNonExistingId_ThrowsBusinessException() {
        String planId = UUID.randomUUID().toString();
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutPlanService.getWorkoutPlanById(planId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", WORKOUT_PLAN_NOT_FOUND);
    }

    @Test
    @DisplayName("Should activate workout plan")
    void activateWorkoutPlan_WithInactivePlan_ActivatesIt() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("trainer-id");

            String planId = UUID.randomUUID().toString();
            TrainerProfile trainer = TrainerProfile.builder().id(UUID.randomUUID().toString()).build();
            WorkoutPlan plan = WorkoutPlan.builder().id(planId).active(false).trainer(trainer).build();

            when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(plan));
            when(messageMapper.message("Workout Plan Activated"))
                    .thenReturn(MessageResponse.builder().message("Workout Plan Activated").build());

            MessageResponse response = workoutPlanService.activateWorkoutPlan(planId);

            assertThat(response).isNotNull();
            assertThat(plan.isActive()).isTrue();
            verify(workoutPlanRepository).save(plan);
        }
    }

    @Test
    @DisplayName("Should throw exception when activating already active plan")
    void activateWorkoutPlan_WhenAlreadyActive_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("trainer-id");

            String planId = UUID.randomUUID().toString();
            WorkoutPlan plan = WorkoutPlan.builder().id(planId).active(true).build();

            when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

            assertThatThrownBy(() -> workoutPlanService.activateWorkoutPlan(planId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", WORKOUT_PLAN_ALREADY_ACTIVATED);
        }
    }

    @Test
    @DisplayName("Should deactivate workout plan")
    void deactivateWorkoutPlan_WithActivePlan_DeactivatesIt() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("trainer-id");

            String planId = UUID.randomUUID().toString();
            WorkoutPlan plan = WorkoutPlan.builder().id(planId).active(true).build();

            when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(plan));
            when(messageMapper.message("Workout Plan Deactivated"))
                    .thenReturn(MessageResponse.builder().message("Workout Plan Deactivated").build());

            MessageResponse response = workoutPlanService.deactivateWorkoutPlan(planId);

            assertThat(response).isNotNull();
            assertThat(plan.isActive()).isFalse();
            verify(workoutPlanRepository).save(plan);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating deactivated plan")
    void updateWorkoutById_WithDeactivatedPlan_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("trainer-id");

            String planId = UUID.randomUUID().toString();
            TrainerProfile trainer = TrainerProfile.builder().id(UUID.randomUUID().toString()).build();
            WorkoutPlan plan = WorkoutPlan.builder().id(planId).active(false).trainer(trainer).build();

            when(trainerProfileRepository.findTrainerProfileByUserId("trainer-id")).thenReturn(Optional.of(trainer));
            when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

            assertThatThrownBy(() -> workoutPlanService.updateWorkoutById(planId,
                    UpdateWorkoutPlanRequest.builder().build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", WORKOUT_PLAN_DEACTIVATED);
        }
    }

    @Test
    @DisplayName("Should throw exception when trainer tries to update another trainer's plan")
    void updateWorkoutById_WithDifferentTrainer_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("trainer-a-id");

            String planId = UUID.randomUUID().toString();
            TrainerProfile currentTrainer = TrainerProfile.builder().id("trainer-a-id").build();
            TrainerProfile otherTrainer = TrainerProfile.builder().id("trainer-b-id").build();
            WorkoutPlan plan = WorkoutPlan.builder().id(planId).active(true).trainer(otherTrainer).build();

            when(trainerProfileRepository.findTrainerProfileByUserId("trainer-a-id")).thenReturn(Optional.of(currentTrainer));
            when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

            assertThatThrownBy(() -> workoutPlanService.updateWorkoutById(planId,
                    UpdateWorkoutPlanRequest.builder().build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", WORKOUT_PLAN_FORBIDDEN);
        }
    }
}
