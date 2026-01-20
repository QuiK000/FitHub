package com.dev.quikkkk.service;

public interface IRateLimitService {
    void checkResendVerificationLimit(String email);

    void checkForgotPasswordLimit(String email);
}
