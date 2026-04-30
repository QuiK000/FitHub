package com.dev.quikkkk.modules.workout.controller;

import com.dev.quikkkk.modules.workout.dto.response.WaitlistResponse;
import com.dev.quikkkk.modules.workout.service.IWaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sessions/{session-id}/waitlist")
@RequiredArgsConstructor
public class WaitlistController {
    private final IWaitlistService waitlistService;

    @PostMapping
    public ResponseEntity<WaitlistResponse> joinWaitlist(@PathVariable("session-id") String sessionId) {
        return ResponseEntity.ok(waitlistService.joinWaitlist(sessionId));
    }

    @DeleteMapping
    public ResponseEntity<Void> leaveWaitlist(@PathVariable("session-id") String sessionId) {
        waitlistService.leaveWaitlist(sessionId);
        return ResponseEntity.noContent().build();
    }
}
