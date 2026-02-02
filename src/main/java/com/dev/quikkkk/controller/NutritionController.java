package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateFoodRequest;
import com.dev.quikkkk.dto.request.UpdateFoodRequest;
import com.dev.quikkkk.dto.response.FoodResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.service.IFoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
public class NutritionController {
    private final IFoodService foodService;

    @PostMapping("/foods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodResponse> createFood(@Valid @RequestBody CreateFoodRequest request) {
        return ResponseEntity.ok(foodService.createFood(request));
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

    @PutMapping("/foods/{food-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodResponse> updateFoodById(
            @PathVariable("food-id") String foodId,
            @Valid @RequestBody UpdateFoodRequest request
    ) {
        return ResponseEntity.ok(foodService.updateFoodById(foodId, request));
    }

    @PatchMapping("/foods/{food-id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deactivateFoodById(@PathVariable("food-id") String foodId) {
        return ResponseEntity.ok(foodService.deactivateFood(foodId));
    }
}