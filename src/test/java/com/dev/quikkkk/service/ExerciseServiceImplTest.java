package com.dev.quikkkk.service;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.fixtures.TestFixtures;
import com.dev.quikkkk.modules.workout.dto.request.CreateExerciseRequest;
import com.dev.quikkkk.modules.workout.dto.request.UpdateExerciseRequest;
import com.dev.quikkkk.modules.workout.dto.response.ExerciseResponse;
import com.dev.quikkkk.modules.workout.entity.Exercise;
import com.dev.quikkkk.modules.workout.enums.ExerciseCategory;
import com.dev.quikkkk.modules.workout.enums.MuscleGroup;
import com.dev.quikkkk.modules.workout.mapper.ExerciseMapper;
import com.dev.quikkkk.modules.workout.repository.IExerciseRepository;
import com.dev.quikkkk.modules.workout.service.impl.ExerciseServiceImpl;
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
import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.EXERCISE_ALREADY_ACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.EXERCISE_ALREADY_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.EXERCISE_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.EXERCISE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExerciseService Tests")
class ExerciseServiceImplTest {

    @Mock
    private IExerciseRepository exerciseRepository;
    @Mock
    private ExerciseMapper exerciseMapper;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Test
    @DisplayName("Should create exercise successfully")
    void createExercise_WithValidRequest_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            CreateExerciseRequest request = CreateExerciseRequest.builder()
                    .name("Bench Press")
                    .category(ExerciseCategory.STRENGTH)
                    .primaryMuscleGroup(MuscleGroup.CHEST)
                    .build();
            Exercise exercise = TestFixtures.createExercise("Bench Press", ExerciseCategory.STRENGTH);
            ExerciseResponse expected = ExerciseResponse.builder().id(exercise.getId()).name("Bench Press").build();

            when(exerciseMapper.toEntity(request, "user-id")).thenReturn(exercise);
            when(exerciseMapper.toResponse(exercise)).thenReturn(expected);

            ExerciseResponse response = exerciseService.createExercise(request);

            assertThat(response).isNotNull();
            verify(exerciseRepository).save(exercise);
        }
    }

    @Test
    @DisplayName("Should find exercise by id")
    void findExerciseById_WithValidId_ReturnsResponse() {
        String exerciseId = UUID.randomUUID().toString();
        Exercise exercise = TestFixtures.createExercise("Squat", ExerciseCategory.STRENGTH);
        exercise.setId(exerciseId);
        ExerciseResponse expected = ExerciseResponse.builder().id(exerciseId).name("Squat").build();

        when(exerciseRepository.findByIdWithSecondaryMuscles(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseMapper.toResponse(exercise)).thenReturn(expected);

        ExerciseResponse response = exerciseService.findExerciseById(exerciseId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(exerciseId);
    }

    @Test
    @DisplayName("Should throw exception when exercise not found by id")
    void findExerciseById_WithNonExistingId_ThrowsBusinessException() {
        String exerciseId = UUID.randomUUID().toString();
        when(exerciseRepository.findByIdWithSecondaryMuscles(exerciseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.findExerciseById(exerciseId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EXERCISE_NOT_FOUND);
    }

    @Test
    @DisplayName("Should activate exercise successfully")
    void activateExercise_WithInactiveExercise_ActivatesIt() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String exerciseId = UUID.randomUUID().toString();
            Exercise exercise = TestFixtures.createExercise("Deadlift", ExerciseCategory.STRENGTH);
            exercise.setId(exerciseId);
            exercise.setActive(false);

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
            when(messageMapper.message("Exercise successfully activated"))
                    .thenReturn(MessageResponse.builder().message("Exercise successfully activated").build());

            MessageResponse response = exerciseService.activateExercise(exerciseId);

            assertThat(response).isNotNull();
            assertThat(exercise.isActive()).isTrue();
            verify(exerciseRepository).save(exercise);
        }
    }

    @Test
    @DisplayName("Should throw exception when activating already active exercise")
    void activateExercise_WhenAlreadyActive_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String exerciseId = UUID.randomUUID().toString();
            Exercise exercise = TestFixtures.createExercise("Push Up", ExerciseCategory.STRENGTH);
            exercise.setId(exerciseId);
            exercise.setActive(true);

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

            assertThatThrownBy(() -> exerciseService.activateExercise(exerciseId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", EXERCISE_ALREADY_ACTIVATED);
        }
    }

    @Test
    @DisplayName("Should deactivate exercise successfully")
    void deactivateExercise_WithActiveExercise_DeactivatesIt() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String exerciseId = UUID.randomUUID().toString();
            Exercise exercise = TestFixtures.createExercise("Pull Up", ExerciseCategory.STRENGTH);
            exercise.setId(exerciseId);
            exercise.setActive(true);

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
            when(messageMapper.message("Exercise successfully deactivated"))
                    .thenReturn(MessageResponse.builder().message("Exercise successfully deactivated").build());

            MessageResponse response = exerciseService.deactivateExercise(exerciseId);

            assertThat(response).isNotNull();
            assertThat(exercise.isActive()).isFalse();
            verify(exerciseRepository).save(exercise);
        }
    }

    @Test
    @DisplayName("Should throw exception when deactivating already inactive exercise")
    void deactivateExercise_WhenAlreadyInactive_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String exerciseId = UUID.randomUUID().toString();
            Exercise exercise = TestFixtures.createExercise("Row", ExerciseCategory.STRENGTH);
            exercise.setId(exerciseId);
            exercise.setActive(false);

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

            assertThatThrownBy(() -> exerciseService.deactivateExercise(exerciseId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", EXERCISE_ALREADY_DEACTIVATED);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating deactivated exercise")
    void updateExercise_WithDeactivatedExercise_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String exerciseId = UUID.randomUUID().toString();
            Exercise exercise = TestFixtures.createExercise("Curl", ExerciseCategory.STRENGTH);
            exercise.setId(exerciseId);
            exercise.setActive(false);

            when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

            assertThatThrownBy(() -> exerciseService.updateExercise(exerciseId,
                    UpdateExerciseRequest.builder().build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", EXERCISE_DEACTIVATED);
        }
    }

    @Test
    @DisplayName("Should get active exercise entity")
    void getActiveExerciseEntity_WithValidId_ReturnsExercise() {
        String exerciseId = UUID.randomUUID().toString();
        Exercise exercise = TestFixtures.createExercise("Bench Press", ExerciseCategory.STRENGTH);
        exercise.setId(exerciseId);
        exercise.setActive(true);

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        Exercise result = exerciseService.getActiveExerciseEntity(exerciseId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(exerciseId);
    }

    @Test
    @DisplayName("Should throw exception when getting inactive exercise entity")
    void getActiveExerciseEntity_WithInactiveExercise_ThrowsBusinessException() {
        String exerciseId = UUID.randomUUID().toString();
        Exercise exercise = TestFixtures.createExercise("Bench Press", ExerciseCategory.STRENGTH);
        exercise.setId(exerciseId);
        exercise.setActive(false);

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        assertThatThrownBy(() -> exerciseService.getActiveExerciseEntity(exerciseId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EXERCISE_DEACTIVATED);
    }
}
