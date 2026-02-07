package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateFoodRequest;
import com.dev.quikkkk.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.dto.request.CreateMealRequest;
import com.dev.quikkkk.dto.request.UpdateFoodRequest;
import com.dev.quikkkk.dto.request.UpdateMealPlanRequest;
import com.dev.quikkkk.dto.response.FoodResponse;
import com.dev.quikkkk.dto.response.MealPlanResponse;
import com.dev.quikkkk.dto.response.MealResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.service.IFoodService;
import com.dev.quikkkk.service.IMealPlanService;
import com.dev.quikkkk.service.IMealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
public class NutritionController {
    private final IFoodService foodService;
    private final IMealPlanService mealPlanService;
    private final IMealService mealService;

    @PostMapping("/foods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodResponse> createFood(@Valid @RequestBody CreateFoodRequest request) {
        return ResponseEntity.ok(foodService.createFood(request));
    }

    @PostMapping("/meal-plans")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MealPlanResponse> createMealPlan(@Valid @RequestBody CreateMealPlanRequest request) {
        return ResponseEntity.ok(mealPlanService.createMealPlan(request));
    }

    @PostMapping("/meal-plans/{meal-plan-id}/meals")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MealResponse> createMeal(
            @Valid @RequestBody CreateMealRequest request,
            @PathVariable("meal-plan-id") String planId
    ) {
        return ResponseEntity.ok(mealService.createMeal(request, planId));
    }

    @GetMapping("/foods")
    public ResponseEntity<PageResponse<FoodResponse>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(foodService.getAllFoods(page, size));
    }

    @GetMapping("/foods/{food-id}")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable("food-id") String foodId) {
        return ResponseEntity.ok(foodService.getFoodById(foodId));
    }

    @GetMapping("/foods/search")
    public ResponseEntity<PageResponse<FoodResponse>> searchFood(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(foodService.searchFoodByQuery(q, page, size));
    }

    @GetMapping("/foods/barcode/{barcode}")
    public ResponseEntity<FoodResponse> getFoodByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(foodService.getFoodByBarcode(barcode));
    }

    @GetMapping("/meal-plans")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageResponse<MealPlanResponse>> getMyMealPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(mealPlanService.getMyMealPlans(page, size));
    }

    @GetMapping("/meal-plans/{meal-plan-id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MealPlanResponse> getMealPlanById(@PathVariable("meal-plan-id") String mealPlanId) {
        return ResponseEntity.ok(mealPlanService.getMealPlanById(mealPlanId));
    }

    @GetMapping("/meal-plans/weekly")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageResponse<MealPlanResponse>> getWeeklyMealPlans(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {
        LocalDate endDate = startDate.plusDays(6);
        return ResponseEntity.ok(mealPlanService.getMealPlansByDateRange(startDate, endDate));
    }

    @GetMapping("/meal-plans/date/{date}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MealPlanResponse> getMealPlanByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(mealPlanService.getMealPlanByDate(date));
    }

    @PutMapping("/foods/{food-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodResponse> updateFoodById(
            @PathVariable("food-id") String foodId,
            @Valid @RequestBody UpdateFoodRequest request
    ) {
        return ResponseEntity.ok(foodService.updateFoodById(foodId, request));
    }

    @PutMapping("/meal-plans/{meal-plan-id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MealPlanResponse> updateMealPlanById(
            @PathVariable("meal-plan-id") String mealPlanId,
            @Valid @RequestBody UpdateMealPlanRequest request
    ) {
        return ResponseEntity.ok(mealPlanService.updateMealPlan(request, mealPlanId));
    }

    @PatchMapping("/foods/{food-id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deactivateFoodById(@PathVariable("food-id") String foodId) {
        return ResponseEntity.ok(foodService.deactivateFood(foodId));
    }

    @PatchMapping("/meals/{meal-id}/complete")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MessageResponse> completeMealById(@PathVariable("meal-id") String mealId) {
        return ResponseEntity.ok(mealService.completeMealById(mealId));
    }
}