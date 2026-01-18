package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.service.IWorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workout-plans")
@RequiredArgsConstructor
public class WorkoutPlanController {
    private final IWorkoutPlanService workoutPlanService;

    @PostMapping
    public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(@Valid @RequestBody CreateWorkoutPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutPlanService.createWorkoutPlan(request));
    }
}
