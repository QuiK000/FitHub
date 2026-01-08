package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateSpecializationRequest;
import com.dev.quikkkk.dto.request.UpdateSpecializationRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.SpecializationResponse;
import com.dev.quikkkk.service.ISpecializationService;
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
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
public class SpecializationController {
    private final ISpecializationService specializationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecializationResponse> create(@Valid @RequestBody CreateSpecializationRequest request) {
        return ResponseEntity.ok(specializationService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<SpecializationResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(specializationService.getAllActive(page, size, search));
    }

    @PutMapping("/{specialization-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecializationResponse> update(
            @PathVariable("specialization-id") String id,
            @RequestBody UpdateSpecializationRequest request
    ) {
        return ResponseEntity.ok(specializationService.update(id, request));
    }

    @PatchMapping("/{specialization-id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecializationResponse> disable(
            @PathVariable("specialization-id") String id
    ) {
        return ResponseEntity.ok(specializationService.disable(id));
    }
}
