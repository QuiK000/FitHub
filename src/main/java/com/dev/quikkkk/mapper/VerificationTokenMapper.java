package com.dev.quikkkk.mapper;

import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.entity.VerificationToken;
import com.dev.quikkkk.enums.TokenType;
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
