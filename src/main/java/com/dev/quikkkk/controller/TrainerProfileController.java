package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.dto.request.UpdateTrainerProfileRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.TrainerProfileResponse;
import com.dev.quikkkk.service.ITrainerProfileService;
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
@RequestMapping("/api/v1/profile/trainer")
@RequiredArgsConstructor
public class TrainerProfileController {
    private final ITrainerProfileService trainerProfileService;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerProfileResponse> createTrainerProfile(
            @Valid @RequestBody CreateTrainerProfileRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerProfileService.createTrainerProfile(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerProfileResponse> getCurrentTrainerProfile() {
        return ResponseEntity.ok(trainerProfileService.getTrainerProfile());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'TRAINER')")
    public ResponseEntity<PageResponse<TrainerProfileResponse>> getTrainers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(trainerProfileService.findAllTrainerProfiles(page, size, search));
    }

    @GetMapping("{trainer-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfileById(@PathVariable("trainer-id") String trainerId) {
        return ResponseEntity.ok(trainerProfileService.getTrainerById(trainerId));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(
            @Valid @RequestBody UpdateTrainerProfileRequest request
    ) {
        return ResponseEntity.ok(trainerProfileService.updateTrainerProfile(request));
    }

    @PatchMapping("/me/deactivate")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<MessageResponse> deactivateTrainerProfile() {
        return ResponseEntity.ok(trainerProfileService.deactivateProfile());
    }

    @PatchMapping("/me/clear")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerProfileResponse> clearTrainerProfile() {
        return ResponseEntity.ok(trainerProfileService.clearProfile());
    }
}
