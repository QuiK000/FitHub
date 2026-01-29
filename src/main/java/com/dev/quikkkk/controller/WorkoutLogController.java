package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.request.UpdateLogWorkoutRequest;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;
import com.dev.quikkkk.service.IWorkoutLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<List<WorkoutLogResponse>> getAllWorkoutLogs() {
        return ResponseEntity.ok(workoutLogService.getAllWorkoutLogs());
    }

    @GetMapping("/{workout-log-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutLogResponse> getWorkoutLogById(@PathVariable("workout-log-id") String workoutLogId) {
        return ResponseEntity.ok(workoutLogService.getWorkoutLogById(workoutLogId));
    }

    @PutMapping("/{workout-log-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINER')")
    public ResponseEntity<WorkoutLogResponse> updateWorkoutLogById(
            @PathVariable("workout-log-id") String workoutLogId,
            @Valid @RequestBody UpdateLogWorkoutRequest request
    ) {
        return ResponseEntity.ok(workoutLogService.updateWorkoutLogById(workoutLogId, request));
    }
}
