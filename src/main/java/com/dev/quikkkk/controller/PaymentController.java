package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreatePaymentRequest;
import com.dev.quikkkk.dto.response.PaymentResponse;
import com.dev.quikkkk.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final IPaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }
}
