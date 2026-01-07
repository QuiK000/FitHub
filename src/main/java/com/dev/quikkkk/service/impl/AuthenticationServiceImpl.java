package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.LoginRequest;
import com.dev.quikkkk.dto.request.RegistrationRequest;
import com.dev.quikkkk.dto.response.AuthenticationResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.UserMapper;
import com.dev.quikkkk.repository.IRoleRepository;
import com.dev.quikkkk.repository.IUserRepository;
import com.dev.quikkkk.service.IAuthenticationService;
import com.dev.quikkkk.service.IEmailService;
import com.dev.quikkkk.service.IJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final static String TOKEN_TYPE = "Bearer";

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IJwtService jwtService;
    private final IEmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());

        User user = findUserByEmail(request.getEmail());

        if (!user.isEnabled()) {
            log.warn("User {} is disabled", request.getEmail());
            throw new RuntimeException("User " + request.getEmail() + " is disabled");
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
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TOKEN_TYPE)
                .build();
    }

    @Override
    public MessageResponse register(RegistrationRequest request) {
        log.info("Register request for email: {}", request.getEmail());

        checkUserEmail(request.getEmail());
        checkPassword(request.getPassword(), request.getConfirmPassword());

        Role defaultRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Role user does not exist"));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);

        User user = userMapper.toUser(request, roles);
        log.info("Saving user: {}", user);

        userRepository.save(user);
        log.info("User {} registered", user.getEmail());

        return messageMapper.message("User registered successfully");
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    }

    private void checkUserEmail(String email) {
        boolean emailExists = userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) throw new RuntimeException("EMAIL_ALREADY_EXISTS");
    }

    private void checkPassword(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) throw new RuntimeException("PASSWORD_MISMATCH");
    }
}
