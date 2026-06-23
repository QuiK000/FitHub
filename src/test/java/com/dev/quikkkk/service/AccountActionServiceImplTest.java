package com.dev.quikkkk.service;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.ratelimit.IRateLimitService;
import com.dev.quikkkk.core.utils.ServiceUtils;
import com.dev.quikkkk.modules.auth.dto.request.ResetPasswordRequest;
import com.dev.quikkkk.modules.auth.entity.VerificationToken;
import com.dev.quikkkk.modules.auth.enums.TokenType;
import com.dev.quikkkk.modules.auth.repository.IVerificationTokenRepository;
import com.dev.quikkkk.modules.auth.service.impl.AccountActionServiceImpl;
import com.dev.quikkkk.modules.auth.service.IVerificationTokenService;
import com.dev.quikkkk.modules.notification.service.IEmailService;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.EMAIL_ALREADY_VERIFIED;
import static com.dev.quikkkk.core.enums.ErrorCode.PASSWORD_MISMATCH;
import static com.dev.quikkkk.core.enums.ErrorCode.VERIFICATION_TOKEN_EXPIRED;
import static com.dev.quikkkk.core.enums.ErrorCode.VERIFICATION_TOKEN_INVALID;
import static com.dev.quikkkk.core.enums.ErrorCode.VERIFICATION_TOKEN_TYPE_INVALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountActionService Tests")
class AccountActionServiceImplTest {

    @Mock
    private IVerificationTokenRepository tokenRepository;
    @Mock
    private ServiceUtils serviceUtils;
    @Mock
    private IVerificationTokenService verificationTokenService;
    @Mock
    private IEmailService emailService;
    @Mock
    private IRateLimitService rateLimitService;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountActionServiceImpl accountActionService;

    @Test
    @DisplayName("Should verify email with valid token")
    void verifyEmail_WithValidToken_EnablesUserAndReturnsSuccess() {
        // given
        String tokenValue = "valid-token";
        User user = User.builder().id(UUID.randomUUID().toString()).email("test@test.com").enabled(false).build();
        VerificationToken verificationToken = VerificationToken.builder()
                .id(UUID.randomUUID().toString())
                .token(tokenValue)
                .type(TokenType.EMAIL_VERIFICATION)
                .used(false)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();

        when(tokenRepository.findByTokenAndUsedFalse(tokenValue)).thenReturn(Optional.of(verificationToken));
        when(messageMapper.message("Email verified successfully!"))
                .thenReturn(MessageResponse.builder().message("Email verified successfully!").build());

        // when
        MessageResponse response = accountActionService.verifyEmail(tokenValue);

        // then
        assertThat(response).isNotNull();
        assertThat(user.isEnabled()).isTrue();
        assertThat(verificationToken.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(tokenRepository).save(verificationToken);
    }

    @Test
    @DisplayName("Should throw exception when verification token is invalid")
    void verifyEmail_WithInvalidToken_ThrowsBusinessException() {
        when(tokenRepository.findByTokenAndUsedFalse("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountActionService.verifyEmail("invalid-token"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", VERIFICATION_TOKEN_INVALID);
    }

    @Test
    @DisplayName("Should throw exception when token type is wrong")
    void verifyEmail_WithWrongTokenType_ThrowsBusinessException() {
        String tokenValue = "wrong-type-token";
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenValue)
                .type(TokenType.PASSWORD_RESET)
                .used(false)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();

        when(tokenRepository.findByTokenAndUsedFalse(tokenValue)).thenReturn(Optional.of(verificationToken));

        assertThatThrownBy(() -> accountActionService.verifyEmail(tokenValue))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", VERIFICATION_TOKEN_TYPE_INVALID);
    }

    @Test
    @DisplayName("Should throw exception when token is expired")
    void verifyEmail_WithExpiredToken_ThrowsBusinessException() {
        String tokenValue = "expired-token";
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenValue)
                .type(TokenType.EMAIL_VERIFICATION)
                .used(false)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .user(user)
                .build();

        when(tokenRepository.findByTokenAndUsedFalse(tokenValue)).thenReturn(Optional.of(verificationToken));

        assertThatThrownBy(() -> accountActionService.verifyEmail(tokenValue))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", VERIFICATION_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("Should resend verification code successfully")
    void resendVerificationCode_WithValidEmail_SendsEmail() {
        String email = "test@test.com";
        User user = User.builder().id(UUID.randomUUID().toString()).email(email).enabled(false).build();
        String verificationToken = "new-verification-token";

        when(serviceUtils.getUserByEmailOrThrow(email)).thenReturn(user);
        when(verificationTokenService.createVerificationCode(TokenType.EMAIL_VERIFICATION, user.getId()))
                .thenReturn(verificationToken);
        when(messageMapper.message("Verification email sent!"))
                .thenReturn(MessageResponse.builder().message("Verification email sent!").build());

        MessageResponse response = accountActionService.resendVerificationCode(email);

        assertThat(response).isNotNull();
        verify(emailService).sendVerificationEmail(email, verificationToken);
    }

    @Test
    @DisplayName("Should throw exception when resending verification for already verified user")
    void resendVerificationCode_WhenAlreadyVerified_ThrowsBusinessException() {
        String email = "test@test.com";
        User user = User.builder().id(UUID.randomUUID().toString()).email(email).enabled(true).build();

        when(serviceUtils.getUserByEmailOrThrow(email)).thenReturn(user);

        assertThatThrownBy(() -> accountActionService.resendVerificationCode(email))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EMAIL_ALREADY_VERIFIED);
    }

    @Test
    @DisplayName("Should send forgot password email")
    void forgotPassword_WithExistingEmail_SendsEmail() {
        String email = "test@test.com";
        User user = User.builder().id(UUID.randomUUID().toString()).email(email).build();
        String resetToken = "reset-token";

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(verificationTokenService.createVerificationCode(TokenType.PASSWORD_RESET, user.getId()))
                .thenReturn(resetToken);
        when(messageMapper.message("If an account with this email exists, a password link has been sent."))
                .thenReturn(MessageResponse.builder().build());

        MessageResponse response = accountActionService.forgotPassword(email);

        assertThat(response).isNotNull();
        verify(emailService).sendForgotPasswordEmail(email, resetToken);
    }

    @Test
    @DisplayName("Should return success even when email does not exist")
    void forgotPassword_WithNonExistingEmail_ReturnsSuccess() {
        String email = "nonexistent@test.com";

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());
        when(messageMapper.message("If an account with this email exists, a password link has been sent."))
                .thenReturn(MessageResponse.builder().build());

        MessageResponse response = accountActionService.forgotPassword(email);

        assertThat(response).isNotNull();
        verify(emailService, never()).sendForgotPasswordEmail(any(), any());
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_WithValidToken_ResetsPassword() {
        String tokenValue = "valid-reset-token";
        String newPassword = "NewPassword123!";
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token(tokenValue)
                .password(newPassword)
                .confirmPassword(newPassword)
                .build();

        User user = User.builder().id(UUID.randomUUID().toString()).email("test@test.com").password("old").build();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenValue)
                .type(TokenType.PASSWORD_RESET)
                .used(false)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();

        when(tokenRepository.findByTokenAndUsedFalse(tokenValue)).thenReturn(Optional.of(verificationToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-new-password");
        when(messageMapper.message("Password has been reset successfully!"))
                .thenReturn(MessageResponse.builder().message("Password has been reset successfully!").build());

        MessageResponse response = accountActionService.resetPassword(request);

        assertThat(response).isNotNull();
        assertThat(user.getPassword()).isEqualTo("encoded-new-password");
        assertThat(verificationToken.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(tokenRepository).save(verificationToken);
    }

    @Test
    @DisplayName("Should throw exception when passwords do not match")
    void resetPassword_WithPasswordMismatch_ThrowsBusinessException() {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("token")
                .password("Password1!")
                .confirmPassword("DifferentPassword!")
                .build();

        assertThatThrownBy(() -> accountActionService.resetPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PASSWORD_MISMATCH);
    }
}
