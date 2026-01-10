package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.service.ITrainingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
