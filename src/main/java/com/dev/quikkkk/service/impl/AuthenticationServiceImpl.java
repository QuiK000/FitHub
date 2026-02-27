package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LoginRequest;
import com.dev.quikkkk.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.dto.request.RegistrationRequest;
import com.dev.quikkkk.dto.response.AuthenticationResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.enums.RoleName;
import com.dev.quikkkk.enums.TokenType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.AuthenticationMapper;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.UserMapper;
import com.dev.quikkkk.repository.IRoleRepository;
import com.dev.quikkkk.repository.IUserRepository;
import com.dev.quikkkk.service.IAuthenticationService;
import com.dev.quikkkk.service.IEmailService;
import com.dev.quikkkk.service.IJwtService;
import com.dev.quikkkk.service.IRateLimitService;
import com.dev.quikkkk.service.ITokenBlacklistService;
import com.dev.quikkkk.service.IVerificationTokenService;
import com.dev.quikkkk.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.dev.quikkkk.enums.ErrorCode.ACCOUNT_DISABLED;
import static com.dev.quikkkk.enums.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.ROLE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.TOKEN_BLACKLISTED;
import static com.dev.quikkkk.enums.ErrorCode.TOKEN_EXPIRED;
import static com.dev.quikkkk.enums.ErrorCode.TOKEN_INVALID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final static String TOKEN_TYPE = "Bearer ";

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IJwtService jwtService;
    private final IEmailService emailService;
    private final ServiceUtils serviceUtils;
    private final IVerificationTokenService verificationTokenService;
    private final ITokenBlacklistService tokenBlacklistService;
    private final IRateLimitService rateLimitService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final AuthenticationMapper authMapper;

    @Override
    @Transactional
    public AuthenticationResponse login(LoginRequest request, String ipAddress) {
        log.info("Login request for email: {}", request.getEmail());
        rateLimitService.checkLoginAttempts(ipAddress);

        try {
            User user = serviceUtils.getUserByEmailOrThrow(request.getEmail());

            if (!user.isEnabled()) {
                log.warn("User {} is disabled", request.getEmail());
                throw new BusinessException(ACCOUNT_DISABLED);
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            log.info("User {} logged successfully", request.getEmail());
            rateLimitService.resetLoginAttempts(ipAddress);

            return authMapper.toResponse(accessToken, refreshToken, TOKEN_TYPE);
        } catch (Exception e) {
            log.warn("Failed login attempt for email: {} from IP: {}",  request.getEmail(), ipAddress);
            rateLimitService.incrementLoginAttempts(ipAddress);
            throw e;
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token request: {}", request);
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) throw new BusinessException(TOKEN_INVALID);
        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) throw new BusinessException(TOKEN_BLACKLISTED);
        if (jwtService.isTokenExpired(refreshToken)) throw new BusinessException(TOKEN_EXPIRED);

        String userId = jwtService.extractUserId(refreshToken);
        User user = serviceUtils.getUserByIdOrThrow(userId);

        tokenBlacklistService.blacklistToken(refreshToken);

        String newAccess = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        return authMapper.toResponse(newAccess, newRefresh, TOKEN_TYPE);
    }

    @Override
    @Transactional
    public MessageResponse register(RegistrationRequest request) {
        log.info("Register request for email: {}", request.getEmail());

        checkUserEmail(request.getEmail());
        Role defaultRole = roleRepository.findByName(RoleName.CLIENT.name())
                .orElseThrow(() -> new BusinessException(ROLE_NOT_FOUND));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);

        User user = userMapper.toUser(request, roles);
        log.info("Saving user: {}", user);

        userRepository.save(user);
        log.info("User {} registered", user.getEmail());

        String token = verificationTokenService.createVerificationCode(TokenType.EMAIL_VERIFICATION, user.getId());
        emailService.sendVerificationEmail(user.getEmail(), token);

        return messageMapper.message("User registered successfully");
    }

    @Override
    public MessageResponse logout(String accessToken) {
        log.info("Logging out user with access token: {}", accessToken);

        tokenBlacklistService.blacklistToken(accessToken);

        log.info("User logged out successfully.");
        return messageMapper.message("User logged out.");
    }

    private void checkUserEmail(String email) {
        boolean emailExists = userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) throw new BusinessException(EMAIL_ALREADY_EXISTS);
    }
}
