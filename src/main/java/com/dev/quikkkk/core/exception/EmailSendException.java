package com.dev.quikkkk.core.exception;

public class EmailSendException extends EmailException {
    public EmailSendException(String message, String recipientEmail, String emailType) {
        super(message, recipientEmail, emailType);
    }

    public EmailSendException(String message, Throwable cause, String recipientEmail, String emailType) {
        super(message, cause, recipientEmail, emailType);
    }
}
