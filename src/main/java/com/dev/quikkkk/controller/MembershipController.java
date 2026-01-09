package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.response.MembershipResponse;
import com.dev.quikkkk.service.IMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
