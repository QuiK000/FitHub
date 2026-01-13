package com.dev.quikkkk.exception;

import lombok.Getter;

@Getter
public class EmailException extends RuntimeException {
    private final String recipientEmail;
    private final String emailType;

    public EmailException(String message) {
        super(message);
        this.recipientEmail = null;
        this.emailType = null;
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
        this.recipientEmail = null;
        this.emailType = null;
    }

    public EmailException(String message, String recipientEmail, String emailType) {
        super(message);
        this.recipientEmail = recipientEmail;
        this.emailType = emailType;
    }

    public EmailException(String message, Throwable cause, String recipientEmail, String emailType) {
        super(message, cause);
        this.recipientEmail = recipientEmail;
        this.emailType = emailType;
    }
}
