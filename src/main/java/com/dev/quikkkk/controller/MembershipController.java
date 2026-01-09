package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.request.ExtendMembershipRequest;
import com.dev.quikkkk.dto.response.MembershipHistoryResponse;
import com.dev.quikkkk.dto.response.MembershipResponse;
import com.dev.quikkkk.dto.response.MembershipValidationResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.service.IMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {
    private final IMembershipService membershipService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> create(
            @Valid @RequestBody CreateMembershipRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.createMembership(request));
    }

    @PatchMapping("/{membership-id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> activate(@PathVariable("membership-id") String membershipId) {
        return ResponseEntity.ok(membershipService.activateMembership(membershipId));
    }

    @PatchMapping("/{membership-id}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> freeze(@PathVariable("membership-id") String membershipId) {
        return ResponseEntity.ok(membershipService.freezeMembership(membershipId));
    }

    @PatchMapping("/{membership-id}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> unfreeze(@PathVariable("membership-id") String membershipId) {
        return ResponseEntity.ok(membershipService.unfreezeMembership(membershipId));
    }

    @PatchMapping("/{membership-id}/extend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> extend(
            @PathVariable("membership-id") String membershipId,
            @RequestBody @Valid ExtendMembershipRequest request
    ) {
        return ResponseEntity.ok(membershipService.extendMembership(membershipId, request));
    }

    @PatchMapping("/{membership-id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> cancel(@PathVariable("membership-id") String membershipId) {
        return ResponseEntity.ok(membershipService.cancelMembership(membershipId));
    }

    @GetMapping("/client/{client-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<MembershipResponse>> getMembershipClient(
            @PathVariable("client-id") String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(membershipService.getMembershipByClientId(page, size, id));
    }

    @GetMapping("/me/active")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MembershipResponse> getClientMembershipActive() {
        return ResponseEntity.ok(membershipService.getMembershipByClientIdAndActive());
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MembershipHistoryResponse> getClientMembershipHistory() {
        return ResponseEntity.ok(membershipService.getMembershipClientHistory());
    }

    @GetMapping("/client/{client-id}/validate")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<MembershipValidationResponse> validateMembership(@PathVariable("client-id") String clientId) {
        return ResponseEntity.ok(membershipService.validateMembership(clientId));
    }
}
