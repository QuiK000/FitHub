package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.enums.TokenType;
import com.dev.quikkkk.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username:contact@fithub.com}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            String verificationLink = frontendUrl + "/verify-email?token=" + token;
            String emailBody =
                    "Welcome to FitHub!\n\n"
                        + "Thank you for registration. Please verify your email address by clicking the link below:\n\n"
                        + verificationLink
                        + "\n\n"
                        + "This link will expire in " + TokenType.EMAIL_VERIFICATION.getTtlMinutes() + " minutes.\n\n"
                        + "If you didn't create this account, please ignore this email. \n\n"
                        + "Best regards,\n"
                        + "FitHub Team";

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("FitHub - Verification email!");
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending email to {}", toEmail, e);
            throw new RuntimeException("Error sending email to " + toEmail, e);
        }
    }
}
