package com.dev.quikkkk.modules.notification.service;

public interface IEmailService {
    void sendVerificationEmail(String toEmail, String token);

    void sendForgotPasswordEmail(String toEmail, String token);
}
