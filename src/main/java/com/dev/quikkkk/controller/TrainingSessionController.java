package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CheckInTrainingSessionRequest;
import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.request.UpdateTrainingSessionRequest;
import com.dev.quikkkk.dto.response.CheckInResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.service.ITrainingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class TrainingSessionController {
    private final ITrainingSessionService trainingSessionService;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainingSessionResponse> createTrainingSession(
            @Valid @RequestBody CreateTrainingSessionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingSessionService.createSession(request));
    }

    @PostMapping("/{session-id}/join")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MessageResponse> joinToSession(@PathVariable("session-id") String sessionId) {
        return ResponseEntity.ok(trainingSessionService.joinToSession(sessionId));
    }

    @PostMapping("/{session-id}/check-in")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<CheckInResponse> checkIn(
            @PathVariable("session-id") String sessionId,
            @RequestBody @Valid CheckInTrainingSessionRequest request
    ) {
        return ResponseEntity.ok(trainingSessionService.checkIn(sessionId, request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TrainingSessionResponse>> getAllTrainingSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(trainingSessionService.getTrainingSessions(page, size, search));
    }

    @PutMapping("/{session-id}")
    public ResponseEntity<TrainingSessionResponse> updateTrainingSession(
            @Valid @RequestBody UpdateTrainingSessionRequest request,
            @PathVariable("session-id") String sessionId
    ) {
        return ResponseEntity.ok(trainingSessionService.updateSession(sessionId, request));
    }
}
