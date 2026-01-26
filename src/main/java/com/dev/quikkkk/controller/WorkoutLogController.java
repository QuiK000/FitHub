package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;
import com.dev.quikkkk.service.IWorkoutLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workout-logs")
@RequiredArgsConstructor
public class WorkoutLogController {
    private final IWorkoutLogService workoutLogService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<WorkoutLogResponse> createWorkoutLog(@RequestBody @Valid LogWorkoutRequest request) {
        return ResponseEntity.ok(workoutLogService.createWorkoutLog(request));
    }
}
