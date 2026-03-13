package com.dev.quikkkk.core.ratelimit;

public interface IRateLimitService {
    void checkResendVerificationLimit(String email);

    void checkForgotPasswordLimit(String email);

    void checkLoginAttempts(String ipAddress);

    void incrementLoginAttempts(String ipAddress);

    void resetLoginAttempts(String ipAddress);
}
