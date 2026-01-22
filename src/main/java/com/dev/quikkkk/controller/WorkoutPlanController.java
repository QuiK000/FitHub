package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.AddExerciseToPlanRequest;
import com.dev.quikkkk.dto.request.AssignWorkoutPlanRequest;
import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.request.ReorderWorkoutPlanExerciseRequest;
import com.dev.quikkkk.dto.request.UpdatePlanExerciseRequest;
import com.dev.quikkkk.dto.request.UpdateWorkoutPlanRequest;
import com.dev.quikkkk.dto.response.ClientWorkoutPlanResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanExerciseResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.enums.DifficultyLevel;
import com.dev.quikkkk.service.IClientWorkoutPlanService;
import com.dev.quikkkk.service.IWorkoutPlanExerciseService;
import com.dev.quikkkk.service.IWorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final IWorkoutPlanExerciseService workoutPlanExerciseService;
    private final IClientWorkoutPlanService clientWorkoutPlanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(@Valid @RequestBody CreateWorkoutPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutPlanService.createWorkoutPlan(request));
    }

    @PostMapping("/{plan-id}/exercises")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutPlanExerciseResponse> addExerciseToPlan(
            @PathVariable("plan-id") String planId,
            @Valid @RequestBody AddExerciseToPlanRequest request
    ) {
        return ResponseEntity.ok(workoutPlanExerciseService.addExerciseToPlan(planId, request));
    }

    @PostMapping("/{plan-id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ClientWorkoutPlanResponse> assignPlanToClient(
            @RequestBody @Valid AssignWorkoutPlanRequest request,
            @PathVariable("plan-id") String planId
    ) {
        return ResponseEntity.ok(clientWorkoutPlanService.assignPlanToClient(request, planId));
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

    @GetMapping("/my-plans")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<PageResponse<WorkoutPlanResponse>> getMyPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(workoutPlanService.getMyPlans(page, size));
    }

    @GetMapping("/trainer/{trainer-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<WorkoutPlanResponse>> getTrainerPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("trainer-id") String trainerId
    ) {
        return ResponseEntity.ok(workoutPlanService.getTrainerPlans(page, size, trainerId));
    }

    @GetMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<PageResponse<ClientWorkoutPlanResponse>> getAssignedPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(clientWorkoutPlanService.getAssignedPlans(page, size));
    }

    @GetMapping("/assignments/{assignment-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<ClientWorkoutPlanResponse> getAssignedPlanById(
            @PathVariable("assignment-id") String assignmentId
    ) {
        return ResponseEntity.ok(clientWorkoutPlanService.getAssignedPlanById(assignmentId));
    }

    @PutMapping("/{workout-plan-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlanById(
            @PathVariable("workout-plan-id") String workoutPlanId,
            @RequestBody @Valid UpdateWorkoutPlanRequest request
    ) {
        return ResponseEntity.ok(workoutPlanService.updateWorkoutById(workoutPlanId, request));
    }

    @PutMapping("/{workout-plan-id}/exercises/{exercise-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutPlanExerciseResponse> updateWorkoutPlanExerciseById(
            @PathVariable("workout-plan-id") String workoutPlanId,
            @PathVariable("exercise-id") String exerciseId,
            @Valid @RequestBody UpdatePlanExerciseRequest request
    ) {
        return ResponseEntity.ok(workoutPlanExerciseService.updatePlanExercise(workoutPlanId, exerciseId, request));
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

    @PatchMapping("/{workout-plan-id}/exercises/reorder")
    public ResponseEntity<MessageResponse> reorderExercises(
            @PathVariable("workout-plan-id") String workoutPlanId,
            @Valid @RequestBody ReorderWorkoutPlanExerciseRequest request
    ) {
        return ResponseEntity.ok(workoutPlanExerciseService.reorderExercises(workoutPlanId, request));
    }

    @DeleteMapping("/{workout-plan-id}/exercises/{exercise-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<MessageResponse> deletePlanExercise(
            @PathVariable("workout-plan-id") String workoutPlanId,
            @PathVariable("exercise-id") String exerciseId,
            @RequestParam Integer day
    ) {
        return ResponseEntity.ok(workoutPlanExerciseService.deletePlanExercise(workoutPlanId, exerciseId, day));
    }
}
