package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.request.UpdateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.enums.DifficultyLevel;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.WorkoutPlanMapper;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.repository.IWorkoutPlanRepository;
import com.dev.quikkkk.service.IWorkoutPlanService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_ALREADY_ACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_ALREADY_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_FORBIDDEN;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanServiceImpl implements IWorkoutPlanService {
    private final IWorkoutPlanRepository workoutPlanRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final WorkoutPlanMapper workoutPlanMapper;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    @CacheEvict(value = "lists", allEntries = true)
    public WorkoutPlanResponse createWorkoutPlan(CreateWorkoutPlanRequest request) {
        TrainerProfile trainer = getTrainerProfile();
        WorkoutPlan plan = workoutPlanMapper.toEntity(request, trainer);

        workoutPlanRepository.save(plan);
        return workoutPlanMapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "lists",
            key = "'workoutPlans:difficulty:' + #difficulty + ':' + #page + ':' + #size"
    )
    public PageResponse<WorkoutPlanResponse> getAllWorkoutPlans(int page, int size, DifficultyLevel difficulty) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<WorkoutPlan> workoutPlanPage = workoutPlanRepository.findWorkoutPlanByDifficulty(pageable, difficulty);

        return PaginationUtils.toPageResponse(workoutPlanPage, workoutPlanMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "workoutPlans", key = "'byId:' + #workoutPlanId")
    public WorkoutPlanResponse getWorkoutPlanById(String workoutPlanId) {
        return workoutPlanRepository.findById(workoutPlanId)
                .map(workoutPlanMapper::toResponse)
                .orElseThrow(() -> new BusinessException(WORKOUT_PLAN_NOT_FOUND));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "workoutPlans", key = "'byId:' + #workoutPlanId"),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public WorkoutPlanResponse updateWorkoutById(String workoutPlanId, UpdateWorkoutPlanRequest request) {
        TrainerProfile trainer = getTrainerProfile();
        WorkoutPlan plan = findWorkoutPlanById(workoutPlanId);

        if (!plan.isActive()) throw new BusinessException(WORKOUT_PLAN_DEACTIVATED);
        if (!plan.getTrainer().getId().equals(trainer.getId())) throw new BusinessException(WORKOUT_PLAN_FORBIDDEN);

        workoutPlanMapper.update(request, plan);
        return workoutPlanMapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "lists",
            key = "'workoutPlans:trainer:' + T(com.dev.quikkkk.utils.SecurityUtils).getCurrentUserId() + ':' + #page + ':' + #size"
    )
    public PageResponse<WorkoutPlanResponse> getMyPlans(int page, int size) {
        TrainerProfile trainer = getTrainerProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<WorkoutPlan> workoutPlanPage = workoutPlanRepository.findWorkoutPlansByTrainerId(pageable, trainer.getId());

        return PaginationUtils.toPageResponse(workoutPlanPage, workoutPlanMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "lists",
            key = "'workoutPlans:trainer:' + #trainerId + ':' + #page + ':' + #size"
    )
    public PageResponse<WorkoutPlanResponse> getTrainerPlans(int page, int size, String trainerId) {
        TrainerProfile trainer = trainerProfileRepository.findById(trainerId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<WorkoutPlan> workoutPlanPage = workoutPlanRepository.findWorkoutPlansByTrainerId(pageable, trainer.getId());

        return PaginationUtils.toPageResponse(workoutPlanPage, workoutPlanMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Caching(evict = {
            @CacheEvict(value = "workoutPlans", key = "'byId:' + #workoutPlanId"),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public MessageResponse activateWorkoutPlan(String workoutPlanId) {
        String userId = SecurityUtils.getCurrentUserId();
        WorkoutPlan plan = findWorkoutPlanById(workoutPlanId);
        if (plan.isActive()) throw new BusinessException(WORKOUT_PLAN_ALREADY_ACTIVATED);

        plan.setLastModifiedBy(userId);
        plan.setActive(true);
        workoutPlanRepository.save(plan);

        return messageMapper.message("Workout Plan Activated");
    }

    @Override
    @Transactional(readOnly = true)
    @Caching(evict = {
            @CacheEvict(value = "workoutPlans", key = "'byId:' + #workoutPlanId"),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public MessageResponse deactivateWorkoutPlan(String workoutPlanId) {
        String userId = SecurityUtils.getCurrentUserId();
        WorkoutPlan plan = findWorkoutPlanById(workoutPlanId);
        if (!plan.isActive()) throw new BusinessException(WORKOUT_PLAN_ALREADY_DEACTIVATED);

        plan.setLastModifiedBy(userId);
        plan.setActive(false);
        workoutPlanRepository.save(plan);

        return messageMapper.message("Workout Plan Deactivated");
    }

    private TrainerProfile getTrainerProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return trainerProfileRepository.findTrainerProfileByUserId(userId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }

    private WorkoutPlan findWorkoutPlanById(String workoutPlanId) {
        return workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new BusinessException(WORKOUT_PLAN_NOT_FOUND));
    }
}
