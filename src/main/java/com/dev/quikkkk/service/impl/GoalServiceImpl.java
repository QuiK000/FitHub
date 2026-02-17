package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.dto.request.UpdateGoalRequest;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Goal;
import com.dev.quikkkk.enums.GoalStatus;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.GoalMapper;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.repository.IGoalRepository;
import com.dev.quikkkk.service.IGoalService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import com.dev.quikkkk.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.dev.quikkkk.enums.ErrorCode.ACTIVE_GOAL_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.enums.ErrorCode.GOAL_ALREADY_COMPLETED;
import static com.dev.quikkkk.enums.ErrorCode.GOAL_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GoalServiceImpl implements IGoalService {
    private final IGoalRepository goalRepository;
    private final ClientProfileUtils clientProfileUtils;
    private final GoalMapper goalMapper;
    private final MessageMapper messageMapper;

    @Override
    public GoalResponse createGoal(CreateGoalRequest request) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        if (goalRepository.existsByClientIdAndGoalTypeAndStatus(
                client.getId(),
                request.getGoalType(),
                GoalStatus.ACTIVE)
        ) throw new BusinessException(ACTIVE_GOAL_ALREADY_EXISTS);

        Goal goal = goalMapper.toEntity(request, client);
        Double initialCurrentValue = request.getCurrentValue() != null
                ? request.getCurrentValue()
                : request.getStartValue();

        goal.trackProgress(initialCurrentValue, null);
        goalRepository.save(goal);

        log.info("Created new goal [{}] for client [{}]", goal.getId(), client.getId());
        return goalMapper.toResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GoalResponse> getGoals(int page, int size) {
        return getGoalsByStatus(page, size, null);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getGoalById(String goalId) {
        Goal goal = getEntityByIdAndValidateAccess(goalId);
        return goalMapper.toResponse(goal);
    }

    @Override
    public GoalResponse updateGoalById(String goalId, UpdateGoalRequest request) {
        Goal goal = getEntityByIdAndValidateAccess(goalId);
        goalMapper.update(goal, request);

        if (request.getStartValue() != null) goal.setStartValue(request.getStartValue());
        boolean progressFieldsChanged = request.getStartValue() != null
                || request.getTargetValue() != null
                || request.getCurrentValue() != null;

        if (progressFieldsChanged) {
            Double valToUse = request.getCurrentValue() != null
                    ? request.getCurrentValue()
                    : goal.getCurrentValue();

            goal.trackProgress(valToUse, null);
        }

        return goalMapper.toResponse(goal);
    }

    @Override
    public GoalResponse updateGoalProgress(String goalId, UpdateGoalProgressRequest request) {
        Goal goal = getEntityByIdAndValidateAccess(goalId);
        goal.trackProgress(request.getCurrentValue(), request.getNotes());

        if (goal.getProgressPercentage() >= 100.0 && goal.getStatus() == GoalStatus.ACTIVE) {
            log.info("Goal [{}] reached 100% progress. Consider prompting user to complete.", goalId);
            goal.complete();
        }

        return goalMapper.toResponse(goal);
    }

    @Override
    public MessageResponse completeGoal(String goalId) {
        Goal goal = getEntityByIdAndValidateAccess(goalId);
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) throw new BusinessException(GOAL_ALREADY_COMPLETED);

        goal.complete();

        log.info("Goal [{}] manually completed by client [{}]", goalId, goal.getClient().getId());
        return messageMapper.message("Goal successfully completed");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GoalResponse> getActiveGoals(int page, int size) {
        return getGoalsByStatus(page, size, GoalStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GoalResponse> getCompletedGoals(int page, int size) {
        return getGoalsByStatus(page, size, GoalStatus.COMPLETED);
    }

    private PageResponse<GoalResponse> getGoalsByStatus(int page, int size, GoalStatus status) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<Goal> goalPage;

        if (status == null) {
            goalPage = goalRepository.findAllByClientId(client.getId(), pageable);
        } else {
            goalPage = goalRepository.findAllByClientIdAndStatus(client.getId(), status, pageable);
        }

        return PaginationUtils.toPageResponse(goalPage, goalMapper::toResponse);
    }

    private Goal getEntityByIdAndValidateAccess(String id) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new BusinessException(GOAL_NOT_FOUND));

        ClientProfile currentClient = clientProfileUtils.getCurrentClientProfile();
        if (!Objects.equals(currentClient.getId(), goal.getClient().getId())) {
            log.warn(
                    "Access denied: User [{}] tried to access goal [{}] owned by [{}]",
                    currentClient.getId(),
                    id,
                    goal.getClient().getId()
            );
            throw new BusinessException(FORBIDDEN_ACCESS);
        }

        return goal;
    }
}
