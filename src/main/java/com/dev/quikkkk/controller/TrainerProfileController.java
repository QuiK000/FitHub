package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.dto.response.TrainerProfileResponse;
import com.dev.quikkkk.service.ITrainerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile/trainer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER')")
public class TrainerProfileController {
    private final ITrainerProfileService trainerProfileService;

    @PostMapping
    public ResponseEntity<TrainerProfileResponse> createTrainerProfile(
            @Valid @RequestBody CreateTrainerProfileRequest request
    ) {
        return ResponseEntity.ok(trainerProfileService.createTrainerProfile(request));
    }
}
