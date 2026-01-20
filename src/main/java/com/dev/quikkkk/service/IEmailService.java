package com.dev.quikkkk.service;

public interface IEmailService {
    void sendVerificationEmail(String toEmail, String token);

    void sendForgotPasswordEmail(String toEmail, String token);
}
