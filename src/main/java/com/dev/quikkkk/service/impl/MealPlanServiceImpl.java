package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.response.MealPlanResponse;
import com.dev.quikkkk.entity.MealPlan;
import com.dev.quikkkk.mapper.MealPlanMapper;
import com.dev.quikkkk.repository.IMealPlanRepository;
import com.dev.quikkkk.service.IMealPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanServiceImpl implements IMealPlanService {
    private final IMealPlanRepository mealPlanRepository;
    private final MealPlanMapper mealPlanMapper;

    @Override
    @Transactional
    public MealPlanResponse createMealPlan(CreateMealPlanRequest request) {
        MealPlan mealPlan = mealPlanMapper.toEntity(request);
        return null;
    }
}
