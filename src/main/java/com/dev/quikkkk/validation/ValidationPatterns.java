package com.dev.quikkkk.validation;

public class ValidationPatterns {
    public static final String PHONE_E164 = "^\\+?[1-9]\\d{9,14}$";
    public static final String EMAIL_BASIC_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String PASSWORD_STRONG_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$";
}
