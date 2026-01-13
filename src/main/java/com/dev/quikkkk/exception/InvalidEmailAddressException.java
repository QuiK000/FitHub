package com.dev.quikkkk.exception;

import lombok.Getter;

@Getter
public class InvalidEmailAddressException extends EmailException {
    private final String invalidEmail;

    public InvalidEmailAddressException(String invalidEmail) {
        super("Invalid email address: " + invalidEmail);
        this.invalidEmail = invalidEmail;
    }
}
