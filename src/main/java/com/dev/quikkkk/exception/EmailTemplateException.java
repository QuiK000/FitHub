package com.dev.quikkkk.exception;

import lombok.Getter;

@Getter
public class EmailTemplateException extends EmailException {
    private final String templateName;

    public EmailTemplateException(String message, String templateName) {
        super(message);
        this.templateName = templateName;
    }

    public EmailTemplateException(String message, Throwable cause, String templateName) {
        super(message, cause);
        this.templateName = templateName;
    }
}
