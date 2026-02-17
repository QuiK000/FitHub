package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Goal;
import com.dev.quikkkk.enums.GoalStatus;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.GoalMapper;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.repository.IGoalRepository;
import com.dev.quikkkk.service.IGoalService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
        Goal goal = goalMapper.toEntity(request, client);

        goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getGoalById(String goalId) {
        Goal goal = getEntityByIdAndValidateAccess(goalId);
        return goalMapper.toResponse(goal);
    }

    @Override
    public GoalResponse updateGoalById(String goalId, UpdateGoalProgressRequest request) {
        Goal goal = getEntityByIdAndValidateAccess(goalId);
        goalMapper.update(goal, request);

        return goalMapper.toResponse(goal);
    }

    @Override
    public MessageResponse completeGoal(String goalId) {
        Goal goal = getEntityByIdAndValidateAccess(goalId);
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) throw new BusinessException(GOAL_ALREADY_COMPLETED);

        goal.setStatus(GoalStatus.COMPLETED);
        return messageMapper.message("Goal successfully completed");
    }

    private Goal getEntityByIdAndValidateAccess(String id) {
        Goal goal = goalRepository.findById(id).orElseThrow(() -> new BusinessException(GOAL_NOT_FOUND));

        ClientProfile currentClient = clientProfileUtils.getCurrentClientProfile();
        if (!Objects.equals(currentClient.getId(), goal.getClient().getId()))
            throw new BusinessException(FORBIDDEN_ACCESS);

        return goal;
    }
}
