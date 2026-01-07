package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.entity.VerificationToken;
import com.dev.quikkkk.enums.TokenType;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.repository.IUserRepository;
import com.dev.quikkkk.repository.IVerificationTokenRepository;
import com.dev.quikkkk.service.IAccountActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountActionServiceImpl implements IAccountActionService {
    private final IVerificationTokenRepository tokenRepository;
    private final IUserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponse verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository
                .findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("INVALID_TOKEN"));

        if (verificationToken.getType() != TokenType.EMAIL_VERIFICATION) {
            log.error("Invalid token type");
            throw new RuntimeException("INVALID_TOKEN_TYPE");
        }

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Invalid token expires at");
            throw new RuntimeException("INVALID_TOKEN_EXPIRES");
        }

        User user = verificationToken.getUser();

        user.setEnabled(true);
        verificationToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(verificationToken);

        return messageMapper.message("Email verified successfully!");
    }
}
