package com.dev.quikkkk.enums;

import lombok.Getter;

@Getter
public enum EmailType {
    VERIFICATION("Email Verification", "verification"),
    PASSWORD_RESET("Password Reset", "password-reset"),
    WELCOME("Welcome Email", "welcome"),
    NOTIFICATION("Notification", "notification"),;

    private final String displayName;
    private final String code;

    EmailType(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }
}
