package com.dev.quikkkk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenType {
    EMAIL_VERIFICATION(1440),
    PASSWORD_RESET(30),
    CHANGE_EMAIL(30);

    private final long ttlMinutes;
}
