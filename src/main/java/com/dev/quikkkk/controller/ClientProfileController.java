package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.dto.request.UpdateClientProfileRequest;
import com.dev.quikkkk.dto.response.ClientProfileResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.service.IClientProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile/client")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class ClientProfileController {
    private final IClientProfileService clientProfileService;

    @PostMapping
    public ResponseEntity<ClientProfileResponse> createProfile(@Valid @RequestBody CreateClientProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientProfileService.createClientProfile(request));
    }

    @GetMapping("/me")
    public ResponseEntity<ClientProfileResponse> getCurrentUserProfile() {
        return ResponseEntity.ok(clientProfileService.getClientProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<MessageResponse> updateUserProfile(@Valid @RequestBody UpdateClientProfileRequest request) {
        return ResponseEntity.ok(clientProfileService.updateClientProfile(request));
    }

    @PatchMapping("/me/deactivate")
    public ResponseEntity<MessageResponse> deactivateUserProfile() {
        return ResponseEntity.ok(clientProfileService.deactivateProfile());
    }

    @PatchMapping("/me/clear")
    public ResponseEntity<ClientProfileResponse> clearUserProfile() {
        return ResponseEntity.ok(clientProfileService.clearProfile());
    }
}
