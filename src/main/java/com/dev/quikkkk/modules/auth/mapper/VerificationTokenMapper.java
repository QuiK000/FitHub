package com.dev.quikkkk.modules.auth.mapper;

import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.auth.entity.VerificationToken;
import com.dev.quikkkk.modules.auth.enums.TokenType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationTokenMapper {
    public VerificationToken toToken(String code, User user, TokenType type) {
        return VerificationToken.builder()
                .token(code)
                .type(type)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(type.getTtlMinutes()))
                .used(false)
                .build();
    }
}
