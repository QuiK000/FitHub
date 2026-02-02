package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateFoodRequest;
import com.dev.quikkkk.dto.response.FoodResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.Food;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.FoodMapper;
import com.dev.quikkkk.repository.IFoodRepository;
import com.dev.quikkkk.service.IFoodService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.FOOD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodServiceImpl implements IFoodService {
    private final IFoodRepository foodRepository;
    private final FoodMapper foodMapper;

    @Override
    @Transactional
    public FoodResponse createFood(CreateFoodRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        Food food = foodMapper.toEntity(request, userId);

        foodRepository.save(food);
        return foodMapper.toResponse(food);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FoodResponse> getAllFoods(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<Food> foodPage = foodRepository.getFoodsWhereActiveIsTrue(pageable);

        return PaginationUtils.toPageResponse(foodPage, foodMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodResponse getFoodById(String id) {
        return foodRepository.findById(id)
                .map(foodMapper::toResponse)
                .orElseThrow(() -> new BusinessException(FOOD_NOT_FOUND));
    }
}
