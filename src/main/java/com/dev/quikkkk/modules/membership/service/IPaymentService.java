package com.dev.quikkkk.modules.membership.service;

import com.dev.quikkkk.modules.membership.dto.request.CreatePaymentRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.modules.membership.dto.response.PaymentResponse;

public interface IPaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request);

    PageResponse<PaymentResponse> getPayments(int page, int size);

    PageResponse<PaymentResponse> getPaymentsByClientId(String clientId, int page, int size);
}
