package com.dev.quikkkk.modules.membership.mapper;

import com.dev.quikkkk.modules.membership.dto.request.CreatePaymentRequest;
import com.dev.quikkkk.modules.membership.dto.response.MembershipShortResponse;
import com.dev.quikkkk.modules.membership.dto.response.PaymentResponse;
import com.dev.quikkkk.modules.membership.entity.Membership;
import com.dev.quikkkk.modules.membership.entity.Payment;
import com.dev.quikkkk.modules.membership.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Currency;

@Service
public class PaymentMapper {
    public Payment toEntity(CreatePaymentRequest request, Membership membership) {
        return Payment.builder()
                .amount(request.getAmount())
                .currency(Currency.getInstance(request.getCurrency()))
                .status(PaymentStatus.PAID)
                .paymentDate(LocalDateTime.now())
                .client(membership.getClient())
                .membership(membership)
                .createdBy(membership.getClient().getId())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .membership(
                        MembershipShortResponse.builder()
                                .membershipId(payment.getMembership().getId())
                                .type(payment.getMembership().getType())
                                .status(payment.getMembership().getStatus())
                                .build()
                )
                .build();
    }
}
