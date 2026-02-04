package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.request.UpdateMealPlanRequest;
import com.dev.quikkkk.dto.response.MealPlanResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.MealPlan;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MealPlanMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMealPlanRepository;
import com.dev.quikkkk.service.IMealPlanService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.DUPLICATE_RESOURCE;
import static com.dev.quikkkk.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.enums.ErrorCode.MEAL_PLAN_NOT_FOUND;

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
        ClientProfile client = getCurrentClientProfile();
        Optional<MealPlan> existing = mealPlanRepository.findByClientIdAndPlanDate(client.getId(), request.getPlanDate());

        if (existing.isPresent()) {
            log.warn("Meal plan already exists for date: {}", request.getPlanDate());
            throw new BusinessException(DUPLICATE_RESOURCE);
        }

        MealPlan mealPlan = mealPlanMapper.toEntity(request, client);
        mealPlanRepository.save(mealPlan);
        log.info(
                "Meal plan created: {} for client: {} for date: {}",
                mealPlan.getId(), client.getId(), mealPlan.getPlanDate()
        );

        return mealPlanMapper.toResponse(mealPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MealPlanResponse> getMyMealPlans(int page, int size) {
        ClientProfile client = getCurrentClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "planDate");
        Page<MealPlan> mealPlanPage = mealPlanRepository.findByClientIdOrderByPlanDateDesc(client.getId(), pageable);

        return PaginationUtils.toPageResponse(mealPlanPage, mealPlanMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MealPlanResponse> getMealPlansByDateRange(LocalDate startDate, LocalDate endDate) {
        ClientProfile client = getCurrentClientProfile();
        List<MealPlan> mealPlans = mealPlanRepository.findByClientIdAndDateRange(
                client.getId(),
                startDate,
                endDate
        );

        List<MealPlanResponse> responses = mealPlans.stream().map(mealPlanMapper::toResponse).toList();

        return new PageResponse<>(
                responses,
                responses.size(),
                1,
                0,
                responses.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlanResponse getMealPlanById(String mealPlanId) {
        MealPlan mealPlan = getMealPlanOrThrow(mealPlanId);
        validateAccess(mealPlan);

        return mealPlanMapper.toResponse(mealPlan);
    }

    @Override
    @Transactional
    public MealPlanResponse updateMealPlan(UpdateMealPlanRequest request, String mealPlanId) {
        MealPlan mealPlan = getMealPlanOrThrow(mealPlanId);
        validateAccess(mealPlan);

        mealPlanMapper.update(mealPlan, request);
        log.info("Meal plan updated: {}", mealPlanId);

        return mealPlanMapper.toResponse(mealPlan);
    }

    private ClientProfile getCurrentClientProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
    }

    private MealPlan getMealPlanOrThrow(String id) {
        return mealPlanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MEAL_PLAN_NOT_FOUND));
    }

    private void validateAccess(MealPlan mealPlan) {
        ClientProfile client = getCurrentClientProfile();
        if (!mealPlan.getClient().getId().equals(client.getId())) {
            log.warn("Access denied for meal plan: {} for user: {}", mealPlan.getId(), client.getId());
            throw new BusinessException(FORBIDDEN_ACCESS);
        }
    }
}
