package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateExerciseRequest;
import com.dev.quikkkk.dto.request.UpdateExerciseRequest;
import com.dev.quikkkk.dto.response.ExerciseResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.ExerciseMapper;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.repository.IExerciseRepository;
import com.dev.quikkkk.service.IExerciseService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_ALREADY_ACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_ALREADY_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseServiceImpl implements IExerciseService {
    private final IExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public ExerciseResponse createExercise(CreateExerciseRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        Exercise exercise = exerciseMapper.toEntity(request, userId);

        exerciseRepository.save(exercise);
        return exerciseMapper.toResponse(exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseResponse findExerciseById(String exerciseId) {
        return exerciseMapper.toResponse(findActiveExerciseOrThrow(exerciseId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExerciseResponse> findAllExercises(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Exercise> exercisePage = exerciseRepository.findActiveWithOptionalSearch(search, pageable);

        return PaginationUtils.toPageResponse(exercisePage, exerciseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExerciseResponse> findAllActiveExercises(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<Exercise> exercisePage = exerciseRepository.findAllExercisesByActiveTrue(pageable);

        return PaginationUtils.toPageResponse(exercisePage, exerciseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExerciseResponse> findAllExercisesByCategory(ExerciseCategory category, int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<Exercise> exercisePage = exerciseRepository.findAllExercisesByActiveTrueAndCategory(category, pageable);

        return PaginationUtils.toPageResponse(exercisePage, exerciseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExerciseResponse> findAllExercisesByMuscleGroup(MuscleGroup muscleGroup, int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Exercise> exercisePage = exerciseRepository.findAllExercisesByActiveTrueAndMuscleGroup(
                muscleGroup,
                pageable
        );

        return PaginationUtils.toPageResponse(exercisePage, exerciseMapper::toResponse);
    }

    @Override
    @Transactional
    public ExerciseResponse updateExercise(String exerciseId, UpdateExerciseRequest request) {
        Exercise exercise = findActiveExerciseOrThrow(exerciseId);
        String userId = SecurityUtils.getCurrentUserId();

        exerciseMapper.update(exercise, request, userId);
        return exerciseMapper.toResponse(exercise);
    }

    @Override
    @Transactional
    public MessageResponse activateExercise(String exerciseId) {
        String userId = SecurityUtils.getCurrentUserId();
        Exercise exercise = findExerciseOrThrow(exerciseId);

        if (exercise.isActive()) throw new BusinessException(EXERCISE_ALREADY_ACTIVATED);

        exercise.setActive(true);
        exercise.setLastModifiedBy(userId);

        exerciseRepository.save(exercise);
        return messageMapper.message("Exercise successfully activated");
    }

    @Override
    @Transactional
    public MessageResponse deactivateExercise(String exerciseId) {
        String userId = SecurityUtils.getCurrentUserId();
        Exercise exercise = findExerciseOrThrow(exerciseId);

        if (!exercise.isActive()) throw new BusinessException(EXERCISE_ALREADY_DEACTIVATED);

        exercise.setActive(false);
        exercise.setLastModifiedBy(userId);

        exerciseRepository.save(exercise);
        return messageMapper.message("Exercise successfully deactivated");
    }

    @Override
    @Transactional
    public MessageResponse deleteExercise(String exerciseId) {
        String userId = SecurityUtils.getCurrentUserId();
        Exercise exercise = findExerciseOrThrow(exerciseId);

        if (!exercise.isActive()) throw new BusinessException(EXERCISE_ALREADY_DEACTIVATED);

        exercise.setActive(false);
        exercise.setLastModifiedBy(userId);

        exerciseRepository.save(exercise);
        return messageMapper.message("Exercise successfully deleted");
    }

    private Exercise findExerciseOrThrow(String exerciseId) {
        return exerciseRepository.findById(exerciseId).orElseThrow(() -> new BusinessException(EXERCISE_NOT_FOUND));
    }

    private Exercise findActiveExerciseOrThrow(String exerciseId) {
        Exercise exercise = findExerciseOrThrow(exerciseId);
        if (!exercise.isActive()) throw new BusinessException(EXERCISE_DEACTIVATED);
        return exercise;
    }
}
