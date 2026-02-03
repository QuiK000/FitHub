package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.response.MealPlanResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.MealPlan;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MealPlanMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMealPlanRepository;
import com.dev.quikkkk.service.IMealPlanService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanServiceImpl implements IMealPlanService {
    private final IMealPlanRepository mealPlanRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final MealPlanMapper mealPlanMapper;

    @Override
    @Transactional
    public MealPlanResponse createMealPlan(CreateMealPlanRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        ClientProfile client = clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
        MealPlan mealPlan = mealPlanMapper.toEntity(request, client);

        mealPlanRepository.save(mealPlan);
        return mealPlanMapper.toResponse(mealPlan);
    }
}
