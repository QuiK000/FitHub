package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    @NotBlank(message = "VALIDATION.CREATE.PAYMENT.MEMBERSHIP_ID.NOT_BLANK")
    private String membershipId;

    @NotNull(message = "VALIDATATION.CREATE.PAYMENT.AMOUNT.NOT_NULL")
    @Positive(message = "VALIDATION.CREATE.PAYMENT.AMOUNT.MUST_BE_POSITIVE")
    private BigDecimal amount;

    @NotBlank(message = "VALIDATION.CREATE.PAYMENT.CURRENCY.NOT_BLANK")
    private String currency;
}
