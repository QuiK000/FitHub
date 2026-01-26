package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.WorkoutLog;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.WorkoutLogMapper;
import com.dev.quikkkk.repository.IExerciseRepository;
import com.dev.quikkkk.repository.IWorkoutLogRepository;
import com.dev.quikkkk.service.IWorkoutLogService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutLogServiceImpl implements IWorkoutLogService {
    private final IWorkoutLogRepository workoutLogRepository;
    private final IExerciseRepository exerciseRepository;
    private final WorkoutLogMapper workoutLogMapper;

    @Override
    @Transactional
    public WorkoutLogResponse createWorkoutLog(LogWorkoutRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new BusinessException(EXERCISE_NOT_FOUND));
        String userId = SecurityUtils.getCurrentUserId();

        WorkoutLog workoutLog = workoutLogMapper.toEntity(request, exercise, userId);

        workoutLogRepository.save(workoutLog);
        return workoutLogMapper.toResponse(workoutLog);
    }
}
