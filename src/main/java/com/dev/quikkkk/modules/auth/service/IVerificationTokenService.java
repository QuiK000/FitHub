package com.dev.quikkkk.modules.auth.service;

import com.dev.quikkkk.modules.auth.enums.TokenType;

public interface IVerificationTokenService {
    String createVerificationCode(TokenType type, String userId);
}
