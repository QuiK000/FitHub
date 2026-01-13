package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.entity.EmailFailureLog;
import com.dev.quikkkk.enums.EmailType;
import com.dev.quikkkk.enums.TokenType;
import com.dev.quikkkk.exception.InvalidEmailAddressException;
import com.dev.quikkkk.repository.IEmailFailureLogRepository;
import com.dev.quikkkk.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final JavaMailSender mailSender;
    private final IEmailFailureLogRepository failureLogRepository;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username:contact@fithub.com}")
    private String fromEmail;

    @Value("${app.email.retry-delay-minutes:5}")
    private int retryDelayMinutes;

    @Override
    @Async("emailTaskExecutor")
    public void sendVerificationEmail(String toEmail, String token) {
        log.debug("Starting async email send to: {}", toEmail);
        validateEmailAddress(toEmail);

        try {
            SimpleMailMessage message = buildVerificationMessage(toEmail, token);
            mailSender.send(message);

            log.info("Verification email successfully sent to: {}", toEmail);
        } catch (MailAuthenticationException e) {
            log.error("Mail server authentication failed for email to: {}. Check credentials.", toEmail, e);
            handleEmailFailure(toEmail, EmailType.VERIFICATION, "Authentication failed", e);
        } catch (MailSendException e) {
            log.error("Failed to send email to: {}. Recipient might be invalid.", toEmail, e);
            handleEmailFailure(toEmail, EmailType.VERIFICATION, "Send failed", e);
        } catch (MailParseException e) {
            log.error("Email parsing error for: {}. Check email format.", toEmail, e);
            throw new InvalidEmailAddressException(toEmail);
        } catch (MailException e) {
            log.error("Mail server error sending email to: {}", toEmail, e);
            handleEmailFailure(toEmail, EmailType.VERIFICATION, "Mail server error", e);
        } catch (Exception e) {
            log.error("Unexpected error sending verification email to: {}", toEmail, e);
            handleEmailFailure(toEmail, EmailType.VERIFICATION, "Unexpected error", e);
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

    private void validateEmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) throw new InvalidEmailAddressException(email);
        if (!EMAIL_PATTERN.matcher(email).matches()) throw new InvalidEmailAddressException(email);
    }

    private void handleEmailFailure(String toEmail, EmailType emailType, String reason, Throwable cause) {
        try {
            EmailFailureLog failureLog = EmailFailureLog.builder()
                    .recipientEmail(toEmail)
                    .emailType(emailType)
                    .failureReason(reason)
                    .errorMessage(cause.getMessage())
                    .attemptCount(1)
                    .retryScheduled(true)
                    .nextRetryAt(LocalDateTime.now().plusMinutes(retryDelayMinutes))
                    .lastAttemptAt(LocalDateTime.now())
                    .createdBy("SYSTEM")
                    .build();

            failureLogRepository.save(failureLog);
            log.info(
                    "Email failure logged for retry: recipient={}, type={}, nextRetry={}",
                    toEmail, emailType, failureLog.getNextRetryAt()
            );
        } catch (Exception ex) {
            log.error("Failed to save email failure log", ex);
        }
    }
}
