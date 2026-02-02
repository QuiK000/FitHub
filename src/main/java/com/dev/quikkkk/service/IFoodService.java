package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateFoodRequest;
import com.dev.quikkkk.dto.response.FoodResponse;
import com.dev.quikkkk.dto.response.PageResponse;

public interface IFoodService {
    FoodResponse createFood(CreateFoodRequest request);

    PageResponse<FoodResponse> getAllFoods(int page, int size);

    FoodResponse getFoodById(String id);
}
