package com.dev.quikkkk.modules.auth.service.impl;

import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.auth.entity.VerificationToken;
import com.dev.quikkkk.modules.auth.enums.TokenType;
import com.dev.quikkkk.modules.auth.mapper.VerificationTokenMapper;
import com.dev.quikkkk.modules.auth.repository.IVerificationTokenRepository;
import com.dev.quikkkk.modules.auth.service.IVerificationTokenService;
import com.dev.quikkkk.core.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenServiceImpl implements IVerificationTokenService {
    private final IVerificationTokenRepository verificationTokenRepository;
    private final ServiceUtils serviceUtils;
    private final VerificationTokenMapper verificationTokenMapper;

    @Override
    @Transactional
    public String createVerificationCode(TokenType type, String userId) {
        User user = serviceUtils.getUserByIdOrThrow(userId);
        String token = UUID.randomUUID().toString();

        verificationTokenRepository.invalidateAllByUserAndType(user, type);

        VerificationToken verificationToken = verificationTokenMapper.toToken(token, user, type);
        verificationTokenRepository.save(verificationToken);

        return token;
    }
}
