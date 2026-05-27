package com.dev.quikkkk.modules.membership.enums;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum PaymentCurrency {
    USD, EUR, UAH,

    TRX, BTC, ETH, USDT;

    @JsonCreator
    public static PaymentCurrency from(String value) {
        return Arrays.stream(values())
                .filter(currency -> currency.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNSUPPORTED_CURRENCY));
    }
}
