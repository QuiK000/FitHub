package com.dev.quikkkk.modules.nutrition.service;

import com.dev.quikkkk.modules.nutrition.dto.request.CreateFoodRequest;
import com.dev.quikkkk.modules.nutrition.dto.request.UpdateFoodRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.FoodResponse;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;

public interface IFoodService {
    FoodResponse createFood(CreateFoodRequest request);

    PageResponse<FoodResponse> getAllFoods(int page, int size);

    FoodResponse getFoodById(String id);

    FoodResponse updateFoodById(String id, UpdateFoodRequest request);

    PageResponse<FoodResponse> searchFoodByQuery(String query, int page, int size);

    FoodResponse getFoodByBarcode(String barcode);

    MessageResponse deactivateFood(String foodId);
}
