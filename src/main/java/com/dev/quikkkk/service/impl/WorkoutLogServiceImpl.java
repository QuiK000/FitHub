package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.ClientWorkoutPlan;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.WorkoutLog;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.WorkoutLogMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IClientWorkoutPlanRepository;
import com.dev.quikkkk.repository.IExerciseRepository;
import com.dev.quikkkk.repository.IWorkoutLogRepository;
import com.dev.quikkkk.service.IWorkoutLogService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_ASSIGNMENT_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_LOG_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutLogServiceImpl implements IWorkoutLogService {
    private final IWorkoutLogRepository workoutLogRepository;
    private final IExerciseRepository exerciseRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IClientWorkoutPlanRepository clientWorkoutPlanRepository;
    private final WorkoutLogMapper workoutLogMapper;

    @Override
    @Transactional
    public WorkoutLogResponse createWorkoutLog(LogWorkoutRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new BusinessException(EXERCISE_NOT_FOUND));
        ClientProfile client = getCurrentClientProfile();
        String userId = SecurityUtils.getCurrentUserId();
        ClientWorkoutPlan activeWorkoutPlan = null;

        if (request.getClientWorkoutPlanId() != null) {
            activeWorkoutPlan = clientWorkoutPlanRepository.findById(request.getClientWorkoutPlanId())
                    .orElseThrow(() -> new BusinessException(CLIENT_ASSIGNMENT_NOT_FOUND));
            if (!activeWorkoutPlan.getClient().getId().equals(client.getId())) {
                throw new BusinessException(CLIENT_ASSIGNMENT_NOT_FOUND);
            }
        }

        WorkoutLog workoutLog = workoutLogMapper.toEntity(request, exercise, activeWorkoutPlan, userId);
        workoutLogRepository.save(workoutLog);

        log.info("Workout log created: {} for client: {}", workoutLog.getId(), client.getId());
        return workoutLogMapper.toResponse(workoutLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutLogResponse> getAllWorkoutLogs() {
        return workoutLogRepository.findAll().stream().map(workoutLogMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutLogResponse getWorkoutLogById(String id) {
        return workoutLogRepository.findById(id).map(workoutLogMapper::toResponse)
                .orElseThrow(() -> new BusinessException(WORKOUT_LOG_NOT_FOUND));
    }

    private ClientProfile getCurrentClientProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
    }
}
