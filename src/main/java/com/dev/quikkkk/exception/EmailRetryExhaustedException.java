package com.dev.quikkkk.exception;

import lombok.Getter;

@Getter
public class EmailRetryExhaustedException extends EmailException {
    private final int maxAttempts;
    private final int actualAttempts;

    public EmailRetryExhaustedException(String recipientEmail, int maxAttempts, int actualAttempts) {
        super(
                String.format("Failed to send email after %d attempts (max: %d)", actualAttempts, maxAttempts),
                recipientEmail,
                "retry-exhausted"
        );
        this.maxAttempts = maxAttempts;
        this.actualAttempts = actualAttempts;
    }
}
