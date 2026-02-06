package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateMealRequest;
import com.dev.quikkkk.dto.request.MealFoodRequest;
import com.dev.quikkkk.dto.response.MealResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Food;
import com.dev.quikkkk.entity.MacroNutrients;
import com.dev.quikkkk.entity.Meal;
import com.dev.quikkkk.entity.MealFood;
import com.dev.quikkkk.entity.MealPlan;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MealFoodMapper;
import com.dev.quikkkk.mapper.MealMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IFoodRepository;
import com.dev.quikkkk.repository.IMealPlanRepository;
import com.dev.quikkkk.repository.IMealRepository;
import com.dev.quikkkk.service.IMealService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.FOOD_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.enums.ErrorCode.MEAL_PLAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealServiceImpl implements IMealService {
    private final IMealRepository mealRepository;
    private final IMealPlanRepository mealPlanRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IFoodRepository foodRepository;
    private final MealMapper mealMapper;
    private final MealFoodMapper mealFoodMapper;

    @Override
    @Transactional
    public MealResponse createMeal(CreateMealRequest request, String mealPlanId) {
        String userId = SecurityUtils.getCurrentUserId();
        MealPlan mealPlan = getMealPlanOrThrow(mealPlanId);
        validateAccess(mealPlan);

        Meal meal = mealMapper.toEntity(request, userId);

        meal.setMealPlan(mealPlan);
        meal.setCompleted(false);

        for (MealFoodRequest foodRequest : request.getFoods()) {
            Food food = foodRepository.findById(foodRequest.getFoodId())
                    .orElseThrow(() -> new BusinessException(FOOD_NOT_FOUND));

            MealFood mealFood = mealFoodMapper.toEntity(food, foodRequest);

            mealFood.setMeal(meal);
            mealFood.setCreatedBy(userId);
            meal.getFoods().add(mealFood);
        }

        calculateMealNutrition(meal);
        Meal savedMeal = mealRepository.save(meal);

        mealPlan.getMeals().add(meal);
        updateMealPlanStatistics(mealPlan);
        mealPlanRepository.save(mealPlan);

        return mealMapper.toResponse(savedMeal);
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

    private void calculateMealNutrition(Meal meal) {
        int totalCalories = meal.getFoods().stream()
                .mapToInt(MealFood::getTotalCalories)
                .sum();

        MacroNutrients totalMacros = calculateTotalMacros(meal.getFoods());

        meal.setCalories(totalCalories);
        meal.setMacros(totalMacros);
    }

    private void updateMealPlanStatistics(MealPlan mealPlan) {
        int totalCalories = mealPlan.getMeals().stream()
                .mapToInt(Meal::getCalories)
                .sum();

        MacroNutrients totalMacros = calculatePlanMacros(mealPlan.getMeals());

        mealPlan.setTotalCalories(totalCalories);
        mealPlan.setMacros(totalMacros);
    }

    private MacroNutrients calculatePlanMacros(Set<Meal> meals) {
        double totalProtein = meals.stream()
                .mapToDouble(m -> m.getMacros().getProtein())
                .sum();

        double totalCarbs = meals.stream()
                .mapToDouble(m -> m.getMacros().getCarbs())
                .sum();

        double totalFats = meals.stream()
                .mapToDouble(m -> m.getMacros().getFats())
                .sum();

        double totalFiber = meals.stream()
                .mapToDouble(m -> m.getMacros().getFiber())
                .sum();

        double totalSugar = meals.stream()
                .mapToDouble(m -> m.getMacros().getSugar())
                .sum();

        return MacroNutrients.builder()
                .protein(totalProtein)
                .carbs(totalCarbs)
                .fats(totalFats)
                .fiber(totalFiber)
                .sugar(totalSugar)
                .build();
    }

    private MacroNutrients calculateTotalMacros(Set<MealFood> mealFoods) {
        double totalProtein = mealFoods.stream()
                .mapToDouble(mf -> mf.getTotalMacros().getProtein())
                .sum();

        double totalCarbs = mealFoods.stream()
                .mapToDouble(mf -> mf.getTotalMacros().getCarbs())
                .sum();

        double totalFats = mealFoods.stream()
                .mapToDouble(mf -> mf.getTotalMacros().getFats())
                .sum();

        double totalFiber = mealFoods.stream()
                .mapToDouble(mf -> mf.getTotalMacros().getFiber())
                .sum();

        double totalSugar = mealFoods.stream()
                .mapToDouble(mf -> mf.getTotalMacros().getSugar())
                .sum();

        return MacroNutrients.builder()
                .protein(totalProtein)
                .carbs(totalCarbs)
                .fats(totalFats)
                .sugar(totalSugar)
                .fiber(totalFiber)
                .build();
    }
}
