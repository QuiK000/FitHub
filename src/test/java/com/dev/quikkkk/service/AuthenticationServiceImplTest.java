package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LoginRequest;
import com.dev.quikkkk.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.dto.request.RegistrationRequest;
import com.dev.quikkkk.dto.response.AuthenticationResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.fixtures.TestFixtures;
import com.dev.quikkkk.mapper.AuthenticationMapper;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.UserMapper;
import com.dev.quikkkk.repository.IRoleRepository;
import com.dev.quikkkk.repository.IUserRepository;
import com.dev.quikkkk.service.impl.AuthenticationServiceImpl;
import com.dev.quikkkk.utils.ServiceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static com.dev.quikkkk.enums.ErrorCode.ACCOUNT_DISABLED;
import static com.dev.quikkkk.enums.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.TOKEN_BLACKLISTED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceImplTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private IJwtService jwtService;
    @Mock
    private IEmailService emailService;
    @Mock
    private ServiceUtils serviceUtils;
    @Mock
    private IVerificationTokenService verificationTokenService;
    @Mock
    private ITokenBlacklistService tokenBlacklistService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private AuthenticationMapper authMapper;
    @Mock
    private IRateLimitService rateLimitService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void login_WithValidCredentials_ReturnsTokens() {
        // given
        String ipAddress = "192.168.0.1";
        LoginRequest request = TestFixtures.createLoginRequest("test@example.com", "password");
        User user = TestFixtures.createClientUser();
        user.setEnabled(true);

        String accessToken = "access.token.here";
        String refreshToken = "refresh.token.here";
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer ")
                .build();

        when(serviceUtils.getUserByEmailOrThrow(request.getEmail())).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(authMapper.toResponse(accessToken, refreshToken, "Bearer ")).thenReturn(expectedResponse);

        // when
        AuthenticationResponse response = authenticationService.login(request, ipAddress);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

        verify(rateLimitService).checkLoginAttempts(ipAddress);
        verify(rateLimitService).resetLoginAttempts(ipAddress);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    @DisplayName("Should throw exception when ip is blocked")
    void login_WhenIpBlocked_ThrowsException() {
        // given
        String ipAddress = "192.168.0.1";
        LoginRequest request = TestFixtures.createLoginRequest("test@example.com", "password");

        doThrow(new BusinessException(ErrorCode.TOO_MANY_REQUESTS))
                .when(rateLimitService).checkLoginAttempts(ipAddress);

        // when & then
        assertThatThrownBy(() -> authenticationService.login(request, ipAddress))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOO_MANY_REQUESTS);

        verify(serviceUtils, never()).getUserByEmailOrThrow(anyString());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Should throw exception when user is disabled")
    void login_WithDisabledAccount_ThrowsBusinessException() {
        // given
        String ipAddress = "192.168.0.1";
        LoginRequest request = TestFixtures.createLoginRequest("test@example.com", "password");
        User user = TestFixtures.createClientUser();
        user.setEnabled(false);

        when(serviceUtils.getUserByEmailOrThrow(request.getEmail())).thenReturn(user);

        // when & then
        assertThatThrownBy(() -> authenticationService.login(request, ipAddress))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ACCOUNT_DISABLED);

        verify(rateLimitService).checkLoginAttempts(ipAddress);
        verify(rateLimitService).incrementLoginAttempts(ipAddress);
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Should throw exception with invalid credentials")
    void login_WithInvalidCredentials_ThrowsException() {
        // given
        String ipAddress = "192.168.0.1";
        LoginRequest request = TestFixtures.createLoginRequest("test@example.com", "wrongPassword");
        User user = TestFixtures.createClientUser();

        when(serviceUtils.getUserByEmailOrThrow(request.getEmail())).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // when & then
        assertThatThrownBy(() -> authenticationService.login(request, ipAddress))
                .isInstanceOf(BadCredentialsException.class);

        verify(rateLimitService).checkLoginAttempts(ipAddress);
        verify(rateLimitService).incrementLoginAttempts(ipAddress);
    }

    @Test
    @DisplayName("Should successfully register new user")
    void register_WithValidData_CreatesUser() {
        // given
        RegistrationRequest request = TestFixtures.createRegistrationRequest();

        Role clientRole = Role.builder().name("CLIENT").build();
        User savedUser = TestFixtures.createClientUser();
        savedUser.setEmail(request.getEmail());

        String verificationToken = "verification-token";

        when(userRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(clientRole));
        when(userMapper.toUser(eq(request), any())).thenReturn(savedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(verificationTokenService.createVerificationCode(any(), any())).thenReturn(verificationToken);
        when(messageMapper.message(anyString())).thenReturn(MessageResponse.builder().message("Success").build());

        // when
        authenticationService.register(request);

        // then
        verify(emailService).sendVerificationEmail(request.getEmail(), verificationToken);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_WithExistingEmail_ThrowsBusinessException() {
        // given
        RegistrationRequest request = TestFixtures.createRegistrationRequest();

        when(userRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EMAIL_ALREADY_EXISTS);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully refresh token")
    void refreshToken_WithValidToken_ReturnsNewTokens() {
        // given
        String oldRefreshToken = "old.refresh.token";
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(oldRefreshToken)
                .build();

        User user = TestFixtures.createClientUser();
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";

        when(jwtService.isRefreshToken(oldRefreshToken)).thenReturn(true);
        when(tokenBlacklistService.isTokenBlacklisted(oldRefreshToken)).thenReturn(false);
        when(jwtService.isTokenExpired(oldRefreshToken)).thenReturn(false);
        when(jwtService.extractUserId(oldRefreshToken)).thenReturn(user.getId());
        when(serviceUtils.getUserByIdOrThrow(user.getId())).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(newRefreshToken);
        when(authMapper.toResponse(newAccessToken, newRefreshToken, "Bearer "))
                .thenReturn(AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build());

        // when
        AuthenticationResponse response = authenticationService.refreshToken(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
        verify(tokenBlacklistService).blacklistToken(oldRefreshToken);
    }

    @Test
    @DisplayName("Should throw exception with blacklisted token")
    void refreshToken_WithBlacklistedToken_ThrowsBusinessException() {
        // given
        String blacklistedToken = "blacklisted.token";
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(blacklistedToken)
                .build();

        when(jwtService.isRefreshToken(blacklistedToken)).thenReturn(true);
        when(tokenBlacklistService.isTokenBlacklisted(blacklistedToken)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationService.refreshToken(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", TOKEN_BLACKLISTED);
    }

    @Test
    @DisplayName("Should successfully logout")
    void logout_WithValidToken_BlacklistsToken() {
        // given
        String accessToken = "access.token";
        MessageResponse expectedResponse = MessageResponse.builder()
                .message("User logged out.")
                .build();

        when(messageMapper.message("User logged out.")).thenReturn(expectedResponse);

        // when
        MessageResponse response = authenticationService.logout(accessToken);

        // then
        assertThat(response).isNotNull();
        verify(tokenBlacklistService).blacklistToken(accessToken);
    }
}
