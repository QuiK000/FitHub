package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreatePaymentRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.PaymentResponse;

public interface IPaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request);

    PageResponse<PaymentResponse> getPayments(int page, int size);

    PageResponse<PaymentResponse> getPaymentsByClientId(String clientId, int page, int size);
}
