package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Goal;
import com.dev.quikkkk.mapper.GoalMapper;
import com.dev.quikkkk.repository.IGoalRepository;
import com.dev.quikkkk.service.IGoalService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements IGoalService {
    private final IGoalRepository goalRepository;
    private final ClientProfileUtils clientProfileUtils;
    private final GoalMapper goalMapper;

    @Override
    @Transactional
    public GoalResponse createGoal(CreateGoalRequest request) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Goal goal = goalMapper.toEntity(request, client);

        goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getGoalById(String goalId) {
        return null;
    }
}
