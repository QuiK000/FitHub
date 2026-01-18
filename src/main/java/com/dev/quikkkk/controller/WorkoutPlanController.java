package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.request.UpdateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.enums.DifficultyLevel;
import com.dev.quikkkk.service.IWorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/workout-plans")
@RequiredArgsConstructor
public class WorkoutPlanController {
    private final IWorkoutPlanService workoutPlanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(@Valid @RequestBody CreateWorkoutPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutPlanService.createWorkoutPlan(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<WorkoutPlanResponse>> getAllWorkoutPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam DifficultyLevel difficulty
    ) {
        return ResponseEntity.ok(workoutPlanService.getAllWorkoutPlans(page, size, difficulty));
    }

    @GetMapping("/{workout-plan-id}")
    public ResponseEntity<WorkoutPlanResponse> getWorkoutPlanById(
            @PathVariable("workout-plan-id") String workoutPlanId
    ) {
        return ResponseEntity.ok(workoutPlanService.getWorkoutPlanById(workoutPlanId));
    }

    @PutMapping("/{workout-plan-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlanById(
            @PathVariable("workout-plan-id") String workoutPlanId,
            @RequestBody @Valid UpdateWorkoutPlanRequest request
    ) {
        return ResponseEntity.ok(workoutPlanService.updateWorkoutById(workoutPlanId, request));
    }

    @PatchMapping("/{workout-plan-id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> activateWorkoutPlan(@PathVariable("workout-plan-id") String workoutPlanId) {
        return ResponseEntity.ok(workoutPlanService.activateWorkoutPlan(workoutPlanId));
    }

    @PatchMapping("/{workout-plan-id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deactivateWorkoutPlan(@PathVariable("workout-plan-id") String workoutPlanId) {
        return ResponseEntity.ok(workoutPlanService.deactivateWorkoutPlan(workoutPlanId));
    }
}
