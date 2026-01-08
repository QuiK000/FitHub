package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.entity.VerificationToken;
import com.dev.quikkkk.enums.TokenType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.repository.IUserRepository;
import com.dev.quikkkk.repository.IVerificationTokenRepository;
import com.dev.quikkkk.service.IAccountActionService;
import com.dev.quikkkk.service.IEmailService;
import com.dev.quikkkk.service.IRateLimitService;
import com.dev.quikkkk.service.IVerificationTokenService;
import com.dev.quikkkk.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.dev.quikkkk.enums.ErrorCode.EMAIL_ALREADY_VERIFIED;
import static com.dev.quikkkk.enums.ErrorCode.VERIFICATION_TOKEN_EXPIRED;
import static com.dev.quikkkk.enums.ErrorCode.VERIFICATION_TOKEN_INVALID;
import static com.dev.quikkkk.enums.ErrorCode.VERIFICATION_TOKEN_TYPE_INVALID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountActionServiceImpl implements IAccountActionService {
    private final IVerificationTokenRepository tokenRepository;
    private final ServiceUtils serviceUtils;
    private final IVerificationTokenService verificationTokenService;
    private final IEmailService emailService;
    private final IRateLimitService rateLimitService;
    private final IUserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponse verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository
                .findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new BusinessException(VERIFICATION_TOKEN_INVALID));

        if (verificationToken.getType() != TokenType.EMAIL_VERIFICATION) {
            log.error("Invalid token type");
            throw new BusinessException(VERIFICATION_TOKEN_TYPE_INVALID);
        }

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Invalid token expires at");
            throw new BusinessException(VERIFICATION_TOKEN_EXPIRED);
        }

        User user = verificationToken.getUser();

        user.setEnabled(true);
        verificationToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(verificationToken);

        return messageMapper.message("Email verified successfully!");
    }

    @Override
    public MessageResponse resendVerificationCode(String email) {
        rateLimitService.checkResendVerificationLimit(email);
        User user = serviceUtils.getUserByEmailOrThrow(email);

        if (user.isEnabled()) throw new BusinessException(EMAIL_ALREADY_VERIFIED);
        String token = verificationTokenService.createVerificationCode(TokenType.EMAIL_VERIFICATION, user.getId());

        emailService.sendVerificationEmail(email, token);
        return messageMapper.message("Verification email sent!");
    }
}
