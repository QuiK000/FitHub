package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.service.IAccountActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account-action")
@RequiredArgsConstructor
public class AccountActionController {
    private final IAccountActionService accountActionService;

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(accountActionService.verifyEmail(token));
    }

    @GetMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerificationCode(@RequestParam String email) {
        return ResponseEntity.ok(accountActionService.resendVerificationCode(email));
    }
}
