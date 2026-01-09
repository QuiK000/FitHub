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
    UNAUTHORIZED_USER("UNAUTHORIZED_USER", "Unauthorized", FORBIDDEN),
    CLIENT_PROFILE_ALREADY_EXISTS("CLIENT_PROFILE_ALREADY_EXISTS", "Client Profile Already Exists", CONFLICT),
    CLIENT_PROFILE_NOT_FOUND("CLIENT_PROFILE_NOT_FOUND", "Client Profile Not Found", NOT_FOUND),
    CLIENT_PROFILE_DEACTIVATED("CLIENT_PROFILE_DEACTIVATED", "Client Profile Deactivated", FORBIDDEN),
    TRAINER_PROFILE_ALREADY_EXISTS("TRAINER_PROFILE_ALREADY_EXISTS", "Trainer Profile Already Exists", CONFLICT),
    TRAINER_PROFILE_NOT_FOUND("TRAINER_PROFILE_NOT_FOUND", "Trainer Profile Not Found", NOT_FOUND),
    TRAINER_PROFILE_DEACTIVATED("TRAINER_PROFILE_DEACTIVATED", "Trainer Profile Deactivated", FORBIDDEN),
    SPECIALIZATION_NOT_FOUND("SPECIALIZATION_NOT_FOUND", "Specialization Not Found", NOT_FOUND),
    SPECIALIZATION_ALREADY_EXISTS("SPECIALIZATION_ALREADY_EXISTS", "Specialization Already Exists", CONFLICT),
    SPECIALIZATION_NOT_FOUND_OR_INACTIVE("SPECIALIZATION_NOT_FOUND_OR_INACTIVE", "Specialization Not Found or Inactive", NOT_FOUND),
    MEMBERSHIP_VISITS_REQUIRED("MEMBERSHIP_VISITS_REQUIRED", "Membership Visits Required", BAD_REQUEST),
    MEMBERSHIP_DURATION_REQUIRED("MEMBERSHIP_DURATION_REQUIRED", "Membership Duration Required", BAD_REQUEST),
    MEMBERSHIP_NOT_FOUND("MEMBERSHIP_NOT_FOUND", "Membership Not Found", NOT_FOUND),
    MEMBERSHIP_ALREADY_ACTIVATED("MEMBERSHIP_ALREADY_ACTIVATED", "Membership Already Activated", FORBIDDEN),
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
