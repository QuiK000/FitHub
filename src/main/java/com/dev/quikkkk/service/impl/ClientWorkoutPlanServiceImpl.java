package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.AssignWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.ClientWorkoutPlanResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.ClientWorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.ClientWorkoutPlanMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IClientWorkoutPlanRepository;
import com.dev.quikkkk.repository.IWorkoutPlanRepository;
import com.dev.quikkkk.service.IClientWorkoutPlanService;
import com.dev.quikkkk.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_ASSIGNMENT_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientWorkoutPlanServiceImpl implements IClientWorkoutPlanService {
    private final IClientWorkoutPlanRepository clientWorkoutPlanRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IWorkoutPlanRepository workoutPlanRepository;
    private final ClientWorkoutPlanMapper clientWorkoutPlanMapper;

    @Override
    @Transactional
    public ClientWorkoutPlanResponse assignPlanToClient(AssignWorkoutPlanRequest request, String workoutPlanId) {
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new BusinessException(WORKOUT_PLAN_NOT_FOUND));
        ClientProfile client = clientProfileRepository.findById(request.getClientId())
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
        ClientWorkoutPlan clientWorkoutPlan = clientWorkoutPlanMapper.toEntity(request, plan, client);

        clientWorkoutPlanRepository.save(clientWorkoutPlan);
        return clientWorkoutPlanMapper.toResponse(clientWorkoutPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClientWorkoutPlanResponse> getAssignedPlans(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<ClientWorkoutPlan> clientWorkoutPlanPage = clientWorkoutPlanRepository.getAllAssignedPlans(pageable);

        return PaginationUtils.toPageResponse(clientWorkoutPlanPage, clientWorkoutPlanMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientWorkoutPlanResponse getAssignedPlanById(String assignedPlanId) {
        return clientWorkoutPlanRepository.findById(assignedPlanId)
                .map(clientWorkoutPlanMapper::toResponse)
                .orElseThrow(() -> new BusinessException(CLIENT_ASSIGNMENT_NOT_FOUND));
    }
}
