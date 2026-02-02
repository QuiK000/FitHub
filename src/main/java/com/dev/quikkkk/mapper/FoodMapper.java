package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateFoodRequest;
import com.dev.quikkkk.dto.request.MacroNutrientsDto;
import com.dev.quikkkk.dto.request.UpdateFoodRequest;
import com.dev.quikkkk.dto.response.FoodResponse;
import com.dev.quikkkk.entity.Food;
import com.dev.quikkkk.entity.MacroNutrients;
import org.springframework.stereotype.Service;

@Service
public class FoodMapper {
    public Food toEntity(CreateFoodRequest request, String userId) {
        return Food.builder()
                .macrosPerServing(
                        MacroNutrients.builder()
                                .protein(request.getMacrosPerServing().getProtein())
                                .sugar(request.getMacrosPerServing().getSugar())
                                .carbs(request.getMacrosPerServing().getCarbs())
                                .fats(request.getMacrosPerServing().getFats())
                                .fiber(request.getMacrosPerServing().getFiber())
                                .build()
                )
                .name(request.getName())
                .brand(request.getBrand())
                .servingSize(request.getServingSize())
                .servingUnit(request.getServingUnit())
                .caloriesPerServing(request.getCaloriesPerServing())
                .barcode(request.getBarcode())
                .active(true)
                .createdBy(userId)
                .build();
    }

    public FoodResponse toResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .brand(food.getBrand())
                .servingSize(food.getServingSize())
                .servingUnit(food.getServingUnit())
                .macrosPerServing(
                        MacroNutrientsDto.builder()
                                .protein(food.getMacrosPerServing().getProtein())
                                .sugar(food.getMacrosPerServing().getSugar())
                                .carbs(food.getMacrosPerServing().getCarbs())
                                .fats(food.getMacrosPerServing().getFats())
                                .fiber(food.getMacrosPerServing().getFiber())
                                .build()
                )
                .caloriesPerServing(food.getCaloriesPerServing())
                .barcode(food.getBarcode())
                .active(food.isActive())
                .build();
    }

    public void update(Food food, String userId, UpdateFoodRequest request) {
        if (request.getName() != null) food.setName(request.getName());
        if (request.getBrand() != null) food.setBrand(request.getBrand());
        if (request.getServingSize() != null) food.setServingSize(request.getServingSize());
        if (request.getServingUnit() != null) food.setServingUnit(request.getServingUnit());
        if (request.getCaloriesPerServing() != null) food.setCaloriesPerServing(request.getCaloriesPerServing());
        if (request.getMacrosPerServing() != null) food.setMacrosPerServing(
                MacroNutrients.builder()
                        .protein(request.getMacrosPerServing().getProtein())
                        .sugar(request.getMacrosPerServing().getSugar())
                        .carbs(request.getMacrosPerServing().getCarbs())
                        .fats(request.getMacrosPerServing().getFats())
                        .fiber(request.getMacrosPerServing().getFiber())
                        .build()
        );
        if (request.getBarcode() != null) food.setBarcode(request.getBarcode());
        food.setLastModifiedBy(userId);
    }
}
