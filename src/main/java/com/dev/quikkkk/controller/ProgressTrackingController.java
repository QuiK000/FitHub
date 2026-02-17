package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.request.UpdateBodyMeasurementRequest;
import com.dev.quikkkk.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.dto.response.MeasurementHistoryResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.service.IBodyMeasurementService;
import com.dev.quikkkk.service.IGoalService;
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
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressTrackingController {
    private final IBodyMeasurementService bodyMeasurementService;
    private final IGoalService goalService;

    @PostMapping("/measurements")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BodyMeasurementResponse> createBodyMeasurement(
            @RequestBody @Valid CreateBodyMeasurementRequest request
    ) {
        return ResponseEntity.ok(bodyMeasurementService.createBodyMeasurement(request));
    }

    @PostMapping("/goals")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        return ResponseEntity.ok(goalService.createGoal(request));
    }

    @GetMapping("/measurements/{measurement-id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BodyMeasurementResponse> getBodyMeasurementById(
            @PathVariable("measurement-id") String measurementId
    ) {
        return ResponseEntity.ok(bodyMeasurementService.getBodyMeasurementById(measurementId));
    }

    @GetMapping("/measurements")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageResponse<BodyMeasurementResponse>> getBodyMeasurements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bodyMeasurementService.getBodyMeasurements(page, size));
    }

    @GetMapping("/measurements/latest")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BodyMeasurementResponse> getLatestBodyMeasurement() {
        return ResponseEntity.ok(bodyMeasurementService.getLatestBodyMeasurement());
    }

    @GetMapping("/measurements/history")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MeasurementHistoryResponse> getHistoryBodyMeasurement() {
        return ResponseEntity.ok(bodyMeasurementService.getHistoryBodyMeasurement());
    }

    @GetMapping("/goals/{goal-id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<GoalResponse> getGoalById(@PathVariable("goal-id") String goalId) {
        return ResponseEntity.ok(goalService.getGoalById(goalId));
    }

    @PutMapping("/measurements/{measurement-id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BodyMeasurementResponse> updateBodyMeasurement(
            @PathVariable("measurement-id") String id,
            @Valid @RequestBody UpdateBodyMeasurementRequest request
    ) {
        return ResponseEntity.ok(bodyMeasurementService.updateBodyMeasurement(request, id));
    }

    @PutMapping("/goals/{goal-id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<GoalResponse> updateGoalById(
            @PathVariable("goal-id") String goalId,
            @Valid @RequestBody UpdateGoalProgressRequest request
    ) {
        return ResponseEntity.ok(goalService.updateGoalById(goalId, request));
    }

    @PatchMapping("/goals/{goal-id}/complete")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MessageResponse> completeGoal(@PathVariable("goal-id") String goalId) {
        return ResponseEntity.ok(goalService.completeGoal(goalId));
    }
}
