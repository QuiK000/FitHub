package com.dev.quikkkk.service;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.workout.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.modules.workout.dto.response.WorkoutLogResponse;
import com.dev.quikkkk.modules.workout.entity.ClientWorkoutPlan;
import com.dev.quikkkk.modules.workout.entity.Exercise;
import com.dev.quikkkk.modules.workout.entity.WorkoutLog;
import com.dev.quikkkk.modules.workout.mapper.WorkoutLogMapper;
import com.dev.quikkkk.modules.workout.repository.IClientWorkoutPlanRepository;
import com.dev.quikkkk.modules.workout.repository.IExerciseRepository;
import com.dev.quikkkk.modules.workout.repository.IWorkoutLogRepository;
import com.dev.quikkkk.modules.workout.service.impl.WorkoutLogServiceImpl;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ASSIGNMENT_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.EXERCISE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.WORKOUT_LOG_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkoutLogService Tests")
class WorkoutLogServiceImplTest {

    @Mock
    private IWorkoutLogRepository workoutLogRepository;
    @Mock
    private IExerciseRepository exerciseRepository;
    @Mock
    private IClientWorkoutPlanRepository clientWorkoutPlanRepository;
    @Mock
    private ITrainerProfileRepository trainerProfileRepository;
    @Mock
    private WorkoutLogMapper workoutLogMapper;
    @Mock
    private ClientProfileUtils clientProfileUtils;

    @InjectMocks
    private WorkoutLogServiceImpl workoutLogService;

    @Test
    @DisplayName("Should create workout log successfully")
    void createWorkoutLog_WithValidRequest_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            ClientProfile client = createClient();
            Exercise exercise = Exercise.builder().id(UUID.randomUUID().toString()).active(true).build();
            LogWorkoutRequest request = LogWorkoutRequest.builder()
                    .exerciseId(exercise.getId())
                    .setsCompleted(3)
                    .repsCompleted(10)
                    .weightUsed(60.0)
                    .build();
            WorkoutLog workoutLog = WorkoutLog.builder().id(UUID.randomUUID().toString()).build();
            WorkoutLogResponse expected = WorkoutLogResponse.builder().id(workoutLog.getId()).build();

            when(exerciseRepository.findById(exercise.getId())).thenReturn(Optional.of(exercise));
            when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
            when(workoutLogMapper.toEntity(request, exercise, null, "user-id")).thenReturn(workoutLog);
            when(workoutLogMapper.toResponse(workoutLog)).thenReturn(expected);

            WorkoutLogResponse response = workoutLogService.createWorkoutLog(request);

            assertThat(response).isNotNull();
            verify(workoutLogRepository).save(workoutLog);
        }
    }

    @Test
    @DisplayName("Should throw exception when exercise not found")
    void createWorkoutLog_WhenExerciseNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            ClientProfile client = createClient();
            LogWorkoutRequest request = LogWorkoutRequest.builder()
                    .exerciseId("nonexistent-exercise")
                    .build();

            when(exerciseRepository.findById("nonexistent-exercise")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutLogService.createWorkoutLog(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", EXERCISE_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should throw exception when assignment not found")
    void createWorkoutLog_WhenAssignmentNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            ClientProfile client = createClient();
            Exercise exercise = Exercise.builder().id(UUID.randomUUID().toString()).active(true).build();
            LogWorkoutRequest request = LogWorkoutRequest.builder()
                    .exerciseId(exercise.getId())
                    .clientWorkoutPlanId("nonexistent-assignment")
                    .build();

            when(exerciseRepository.findById(exercise.getId())).thenReturn(Optional.of(exercise));
            when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
            when(clientWorkoutPlanRepository.findById("nonexistent-assignment")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutLogService.createWorkoutLog(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CLIENT_ASSIGNMENT_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should get workout log by id")
    void getWorkoutLogById_WithValidId_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::isTrainer).thenReturn(false);

            String logId = UUID.randomUUID().toString();
            WorkoutLog workoutLog = WorkoutLog.builder().id(logId).build();
            WorkoutLogResponse expected = WorkoutLogResponse.builder().id(logId).build();

            when(workoutLogRepository.findById(logId)).thenReturn(Optional.of(workoutLog));
            when(workoutLogMapper.toResponse(workoutLog)).thenReturn(expected);

            WorkoutLogResponse response = workoutLogService.getWorkoutLogById(logId);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(logId);
        }
    }

    @Test
    @DisplayName("Should throw exception when workout log not found")
    void getWorkoutLogById_WithNonExistingId_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::isTrainer).thenReturn(false);

            String logId = UUID.randomUUID().toString();
            when(workoutLogRepository.findById(logId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workoutLogService.getWorkoutLogById(logId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", WORKOUT_LOG_NOT_FOUND);
        }
    }

    private ClientProfile createClient() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        return ClientProfile.builder().id(UUID.randomUUID().toString()).user(user).build();
    }
}
