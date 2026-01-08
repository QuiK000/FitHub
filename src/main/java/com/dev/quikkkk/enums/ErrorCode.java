package com.dev.quikkkk.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with id %s", NOT_FOUND),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid Credentials", UNAUTHORIZED),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "Account Disabled", FORBIDDEN),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token Expired", UNAUTHORIZED),
    TOKEN_INVALID("TOKEN_INVALID", "Invalid Token", UNAUTHORIZED),
    TOKEN_BLACKLISTED("TOKEN_BLACKLISTED", "Token Blacklisted", UNAUTHORIZED),
    REFRESH_TOKEN_REQUIRED("REFRESH_TOKEN_REQUIRED", "Token Required", BAD_REQUEST),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "Invalid Refresh Token", UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email Already Exists", CONFLICT),
    EMAIL_ALREADY_VERIFIED("EMAIL_ALREADY_VERIFIED", "Email Already Verified", BAD_REQUEST),
    VERIFICATION_TOKEN_INVALID("VERIFICATION_TOKEN_INVALID", "Verification Token Invalid", BAD_REQUEST),
    VERIFICATION_TOKEN_EXPIRED("VERIFICATION_TOKEN_EXPIRED", "Verification Token Expired", BAD_REQUEST),
    VERIFICATION_TOKEN_TYPE_INVALID("VERIFICATION_TOKEN_TYPE_INVALID", "Verification Token Type Invalid", BAD_REQUEST),
    RESEND_TOO_FREQUENT("RESEND_TOO_FREQUENT", "Verification email was sent recently. Try again later", TOO_MANY_REQUESTS),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation failed", BAD_REQUEST),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "Password and confirm password do not match", BAD_REQUEST),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
    }
