package com.dev.quikkkk.modules.membership.dto.response;

import com.dev.quikkkk.modules.membership.enums.PaymentCurrency;
import com.dev.quikkkk.modules.membership.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private String id;
    private BigDecimal amount;
    private PaymentCurrency currency;
    private PaymentStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;
    private MembershipShortResponse membership;
}
