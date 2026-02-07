package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateMealRequest;
import com.dev.quikkkk.dto.request.MacroNutrientsDto;
import com.dev.quikkkk.dto.request.MealFoodRequest;
import com.dev.quikkkk.dto.request.UpdateMealRequest;
import com.dev.quikkkk.dto.response.FoodShortResponse;
import com.dev.quikkkk.dto.response.MealFoodResponse;
import com.dev.quikkkk.dto.response.MealResponse;
import com.dev.quikkkk.entity.Food;
import com.dev.quikkkk.entity.Meal;
import com.dev.quikkkk.entity.MealFood;
import com.dev.quikkkk.entity.MacroNutrients;
import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.repository.IFoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealMapper {

    private final IFoodRepository foodRepository;
    private final MealFoodMapper mealFoodMapper;

    public Meal toEntity(CreateMealRequest request, String userId) {
        return Meal.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getMealType())
                .mealTime(request.getMealTime())
                .createdBy(userId)
                .build();
    }

    public MealResponse toResponse(Meal meal) {
        return MealResponse.builder()
                .id(meal.getId())
                .mealType(meal.getType())
                .mealTime(meal.getMealTime())
                .name(meal.getName())
                .description(meal.getDescription())
                .calories(meal.getCalories())
                .macros(mapMacros(meal.getMacros()))
                .foods(meal.getFoods().stream()
                        .map(this::toMealFoodResponse)
                        .toList())
                .completed(meal.isCompleted())
                .build();
    }

    private MealFoodResponse toMealFoodResponse(MealFood mealFood) {
        return MealFoodResponse.builder()
                .id(mealFood.getId())
                .food(FoodShortResponse.builder()
                        .id(mealFood.getFood().getId())
                        .name(mealFood.getFood().getName())
                        .brand(mealFood.getFood().getBrand())
                        .servingUnit(mealFood.getFood().getServingUnit())
                        .build())
                .servings(mealFood.getServings())
                .totalCalories(mealFood.getTotalCalories())
                .totalMacros(mapMacros(mealFood.getTotalMacros()))
                .build();
    }

    public void update(UpdateMealRequest request, Meal meal, String userId) {
        if (request.getMealType() != null) meal.setType(request.getMealType());
        if (request.getMealTime() != null) meal.setMealTime(request.getMealTime());
        if (request.getName() != null) meal.setName(request.getName());
        if (request.getDescription() != null) meal.setDescription(request.getDescription());

        if (request.getFoods() != null) {
            meal.getFoods().clear();

            for (MealFoodRequest foodRequest : request.getFoods()) {
                Food food = foodRepository.findById(foodRequest.getFoodId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.FOOD_NOT_FOUND));

                MealFood mealFood = mealFoodMapper.toEntity(food, foodRequest);
                mealFood.setMeal(meal);
                mealFood.setCreatedBy(userId);

                meal.getFoods().add(mealFood);
            }
        }
    }

    private MacroNutrientsDto mapMacros(MacroNutrients macros) {
        if (macros == null) return null;
        return MacroNutrientsDto.builder()
                .protein(macros.getProtein())
                .fats(macros.getFats())
                .fiber(macros.getFiber())
                .sugar(macros.getSugar())
                .carbs(macros.getCarbs())
                .build();
    }
}