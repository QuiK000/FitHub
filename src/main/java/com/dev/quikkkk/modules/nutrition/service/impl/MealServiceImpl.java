package com.dev.quikkkk.modules.nutrition.service.impl;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.functional.MacroExtractor;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.nutrition.dto.request.CreateMealRequest;
import com.dev.quikkkk.modules.nutrition.dto.request.MealFoodRequest;
import com.dev.quikkkk.modules.nutrition.dto.request.UpdateMealRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.MealResponse;
import com.dev.quikkkk.modules.nutrition.entity.Food;
import com.dev.quikkkk.modules.nutrition.entity.MacroNutrients;
import com.dev.quikkkk.modules.nutrition.entity.Meal;
import com.dev.quikkkk.modules.nutrition.entity.MealFood;
import com.dev.quikkkk.modules.nutrition.entity.MealPlan;
import com.dev.quikkkk.modules.nutrition.mapper.MealFoodMapper;
import com.dev.quikkkk.modules.nutrition.mapper.MealMapper;
import com.dev.quikkkk.modules.nutrition.repository.IFoodRepository;
import com.dev.quikkkk.modules.nutrition.repository.IMealPlanRepository;
import com.dev.quikkkk.modules.nutrition.repository.IMealRepository;
import com.dev.quikkkk.modules.nutrition.service.IMealService;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.dev.quikkkk.core.enums.ErrorCode.FOOD_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.core.enums.ErrorCode.MEAL_ALREADY_COMPLETED;
import static com.dev.quikkkk.core.enums.ErrorCode.MEAL_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.MEAL_PLAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealServiceImpl implements IMealService {
    private final IMealRepository mealRepository;
    private final IMealPlanRepository mealPlanRepository;
    private final IFoodRepository foodRepository;
    private final MealMapper mealMapper;
    private final MealFoodMapper mealFoodMapper;
    private final MessageMapper messageMapper;
    private final ClientProfileUtils clientProfileUtils;

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

    @Override
    @Transactional
    public MealResponse updateMeal(String mealId, UpdateMealRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        Meal meal = getMealOrThrow(mealId);
        validateAccess(meal);

        if (meal.isCompleted()) throw new BusinessException(MEAL_ALREADY_COMPLETED);

        mealMapper.update(request, meal, userId);
        calculateMealNutrition(meal);

        Meal savedMeal = mealRepository.save(meal);
        MealPlan mealPlan = meal.getMealPlan();

        updateMealPlanStatistics(mealPlan);
        mealPlanRepository.save(mealPlan);

        return mealMapper.toResponse(savedMeal);
    }

    @Override
    @Transactional
    public MessageResponse completeMealById(String mealId) {
        Meal meal = getMealOrThrow(mealId);
        validateAccess(meal);

        if (meal.isCompleted()) throw new BusinessException(MEAL_ALREADY_COMPLETED);

        meal.setCompleted(true);
        meal.setLastModifiedBy(meal.getCreatedBy());

        mealRepository.save(meal);
        return messageMapper.message("Meal was successfully completed");
    }

    private MealPlan getMealPlanOrThrow(String id) {
        return mealPlanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MEAL_PLAN_NOT_FOUND));
    }

    private Meal getMealOrThrow(String id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MEAL_NOT_FOUND));
    }

    private void validateAccess(MealPlan mealPlan) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        if (!mealPlan.getClient().getId().equals(client.getId())) {
            log.warn("Access denied for meal plan: {} for user: {}", mealPlan.getId(), client.getId());
            throw new BusinessException(FORBIDDEN_ACCESS);
        }
    }

    private void validateAccess(Meal meal) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        if (!meal.getMealPlan().getClient().getId().equals(client.getId())) {
            log.warn("Access denied for meal {} for client {}", meal.getId(), client.getId());
            throw new BusinessException(FORBIDDEN_ACCESS);
        }
    }

    private void calculateMealNutrition(Meal meal) {
        int totalCalories = meal.getFoods().stream()
                .mapToInt(mf -> mf.getTotalCalories() != null ? mf.getTotalCalories() : 0)
                .sum();

        meal.setCalories(totalCalories);
        meal.setMacros(calculateTotalMacros(meal.getFoods()));
    }

    private void updateMealPlanStatistics(MealPlan mealPlan) {
        int totalCalories = mealPlan.getMeals().stream()
                .mapToInt(mf -> mf.getCalories() != null ? mf.getCalories() : 0)
                .sum();

        mealPlan.setTotalCalories(totalCalories);
        mealPlan.setMacros(calculatePlanMacros(mealPlan.getMeals()));
    }

    private MacroNutrients calculatePlanMacros(Set<Meal> meals) {
        double protein = meals.stream()
                .mapToDouble(m -> safeDouble(m.getMacros(), MacroNutrients::getProtein)).sum();
        double carbs = meals.stream()
                .mapToDouble(m -> safeDouble(m.getMacros(), MacroNutrients::getCarbs)).sum();
        double fats = meals.stream()
                .mapToDouble(m -> safeDouble(m.getMacros(), MacroNutrients::getFats)).sum();
        double fiber = meals.stream()
                .mapToDouble(m -> safeDouble(m.getMacros(), MacroNutrients::getFiber)).sum();
        double sugar = meals.stream()
                .mapToDouble(m -> safeDouble(m.getMacros(), MacroNutrients::getSugar)).sum();

        return MacroNutrients.builder()
                .protein(protein)
                .carbs(carbs)
                .fats(fats)
                .fiber(fiber)
                .sugar(sugar)
                .build();
    }

    private MacroNutrients calculateTotalMacros(Set<MealFood> mealFoods) {
        double protein = mealFoods.stream()
                .mapToDouble(mf -> safeDouble(mf.getTotalMacros(), MacroNutrients::getProtein)).sum();
        double carbs = mealFoods.stream()
                .mapToDouble(mf -> safeDouble(mf.getTotalMacros(), MacroNutrients::getCarbs)).sum();
        double fats = mealFoods.stream()
                .mapToDouble(mf -> safeDouble(mf.getTotalMacros(), MacroNutrients::getFats)).sum();
        double fiber = mealFoods.stream()
                .mapToDouble(mf -> safeDouble(mf.getTotalMacros(), MacroNutrients::getFiber)).sum();
        double sugar = mealFoods.stream()
                .mapToDouble(mf -> safeDouble(mf.getTotalMacros(), MacroNutrients::getSugar)).sum();

        return MacroNutrients.builder()
                .protein(protein)
                .carbs(carbs)
                .fats(fats)
                .fiber(fiber)
                .sugar(sugar)
                .build();
    }

    private double safeDouble(MacroNutrients macros, MacroExtractor extractor) {
        if (macros == null) return 0.0;
        Double value = extractor.get(macros);
        return value != null ? value : 0.0;
    }
}
