package com.dev.quikkkk.service;

public interface IEmailService {
    void sendVerificationEmail(String toEmail, String token);
}
