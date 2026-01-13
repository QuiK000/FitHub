package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.enums.TokenType;
import com.dev.quikkkk.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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
    @Async("emailTaskExecutor")
    public void sendVerificationEmail(String toEmail, String token) {
        log.debug("Starting async email send to: {}", toEmail);

        try {
            SimpleMailMessage message = buildVerificationMessage(toEmail, token);
            mailSender.send(message);

            log.info("Verification email successfully sent to: {}", toEmail);
        } catch (MailException e) {
            log.error("Mail server error sending email to: {}. Error: {}", toEmail, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {}", toEmail, e);
        }
    }

    private SimpleMailMessage buildVerificationMessage(String toEmail, String token) {
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        String emailBody = String.format("""
                Welcome to FitHub!
                
                Thank you for registration. Please verify your email address by clicking the link below:
                
                %s
                
                This link will expire in %d minutes.
                
                If you didn't create this account, please ignore this email.
                
                Best regards,
                FitHub Team
                """,
                verificationLink,
                TokenType.EMAIL_VERIFICATION.getTtlMinutes());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("FitHub - Verify Your Email");
        message.setText(emailBody);

        return message;
    }
}
