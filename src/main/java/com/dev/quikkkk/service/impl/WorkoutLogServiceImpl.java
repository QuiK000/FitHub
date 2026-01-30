package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.request.UpdateLogWorkoutRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.ClientWorkoutPlan;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.WorkoutLog;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.WorkoutLogMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IClientWorkoutPlanRepository;
import com.dev.quikkkk.repository.IExerciseRepository;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.repository.IWorkoutLogRepository;
import com.dev.quikkkk.service.IWorkoutLogService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_ASSIGNMENT_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_LOG_ACCESS_DENIED;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_LOG_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutLogServiceImpl implements IWorkoutLogService {
    private final IWorkoutLogRepository workoutLogRepository;
    private final IExerciseRepository exerciseRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IClientWorkoutPlanRepository clientWorkoutPlanRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
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
    public PageResponse<WorkoutLogResponse> getAllWorkoutLogs(int page, int size) {
        log.info("Admin fetching all workout logs, page: {}, size: {}", page, size);
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "workoutDate");
        Page<WorkoutLog> workoutLogPage = workoutLogRepository.findAll(pageable);

        return PaginationUtils.toPageResponse(workoutLogPage, workoutLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutLogResponse getWorkoutLogById(String id) {
        WorkoutLog workoutLog = getWorkoutLogByIdOrThrow(id);
        validateTrainerAccessToWorkoutLog(workoutLog);

        return workoutLogMapper.toResponse(workoutLog);
    }

    @Override
    @Transactional
    public WorkoutLogResponse updateWorkoutLogById(String id, UpdateLogWorkoutRequest request) {
        WorkoutLog workoutLog = getWorkoutLogByIdOrThrow(id);
        validateTrainerAccessToWorkoutLog(workoutLog);

        workoutLogMapper.update(request, workoutLog, SecurityUtils.getCurrentUserId());
        return workoutLogMapper.toResponse(workoutLog);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkoutLogResponse> getMyWorkoutLogs(int page, int size) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Client fetching own workout logs, userId: {}, page: {}, size: {}", currentUserId, page, size);

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "workoutDate");
        Page<WorkoutLog> workoutLogPageable = workoutLogRepository.findAllByCreatedBy(currentUserId, pageable);

        return PaginationUtils.toPageResponse(workoutLogPageable, workoutLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkoutLogResponse> getLogsByAssignment(String assignmentId, int page, int size) {
        log.info("Fetching logs for assignment: {}, page: {}, size: {}", assignmentId, page, size);
        ClientWorkoutPlan assignment = clientWorkoutPlanRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(CLIENT_ASSIGNMENT_NOT_FOUND));

        validateTrainerAccessToAssignment(assignment);

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "workoutDate");
        Page<WorkoutLog> workoutLogPage = workoutLogRepository.findByAssignmentId(assignmentId, pageable);

        return PaginationUtils.toPageResponse(workoutLogPage, workoutLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkoutLogResponse> getLogsByExercise(String exerciseId, int page, int size) {
        log.info("Fetching logs for exercise: {}, page: {}, size: {}", exerciseId, page, size);

        exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new BusinessException(EXERCISE_NOT_FOUND));

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "workoutDate");
        Page<WorkoutLog> workoutLogPage = workoutLogRepository.findByExerciseId(exerciseId, pageable);

        return PaginationUtils.toPageResponse(workoutLogPage, workoutLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkoutLogResponse> getLogsByDateRange(LocalDate from, LocalDate to, int page, int size) {
        log.info("Fetching logs by date range: {} to {}, page: {}, size: {}", from, to, page, size);

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "workoutDate");
        Page<WorkoutLog> workoutLogPage;

        if (SecurityUtils.isAdmin()) {
            workoutLogPage = workoutLogRepository.findByDateRangeForAdmin(fromDateTime, toDateTime, pageable);
        } else if (SecurityUtils.isTrainer()) {
            TrainerProfile trainer = getCurrentTrainerProfile();
            workoutLogPage = workoutLogRepository.findByDateRangeForTrainer(
                    fromDateTime,
                    toDateTime,
                    trainer.getId(),
                    pageable
            );
        } else {
            String userId = SecurityUtils.getCurrentUserId();
            workoutLogPage = workoutLogRepository.findByDateRangeAndUserId(
                    fromDateTime, toDateTime, userId, pageable
            );
        }

        return PaginationUtils.toPageResponse(workoutLogPage, workoutLogMapper::toResponse);
    }

    private ClientProfile getCurrentClientProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
    }

    private TrainerProfile getCurrentTrainerProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return trainerProfileRepository.findTrainerProfileByUserId(userId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }

    private WorkoutLog getWorkoutLogByIdOrThrow(String id) {
        return workoutLogRepository.findById(id)
                .orElseThrow(() -> new BusinessException(WORKOUT_LOG_NOT_FOUND));
    }

    private void validateTrainerAccessToWorkoutLog(WorkoutLog workoutLog) {
        if (!SecurityUtils.isTrainer()) return;
        if (workoutLog.getClientWorkoutPlan() == null) throw new BusinessException(WORKOUT_LOG_ACCESS_DENIED);

        TrainerProfile trainer = getCurrentTrainerProfile();
        String logTrainerId = workoutLog.getClientWorkoutPlan().getWorkoutPlan().getTrainer().getId();

        if (!logTrainerId.equals(trainer.getId())) {
            throw new BusinessException(WORKOUT_LOG_ACCESS_DENIED);
        }
    }

    private void validateTrainerAccessToAssignment(ClientWorkoutPlan assignment) {
        if (!SecurityUtils.isTrainer()) return;
        TrainerProfile trainer = getCurrentTrainerProfile();
        String assignmentTrainerId = assignment.getWorkoutPlan().getTrainer().getId();

        if (!assignmentTrainerId.equals(trainer.getId())) throw new BusinessException(WORKOUT_LOG_ACCESS_DENIED);
    }
}
