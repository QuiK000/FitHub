package com.dev.quikkkk.service;

import com.dev.quikkkk.enums.TokenType;

public interface IVerificationTokenService {
    String createVerificationCode(TokenType type, String userId);
}
