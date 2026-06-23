package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.auth.service.IJwtService;
import com.dev.quikkkk.modules.auth.service.impl.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenBlacklistService Tests")
class TokenBlacklistServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private IJwtService jwtService;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Test
    @DisplayName("Should blacklist token successfully")
    void blacklistToken_WithValidToken_StoresInRedis() {
        String token = "valid.jwt.token";
        Date futureExpiration = new Date(System.currentTimeMillis() + 3600000);

        when(jwtService.extractExpiration(token)).thenReturn(futureExpiration);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenBlacklistService.blacklistToken(token);

        verify(valueOperations).set(anyString(), eq("1"), any(Duration.class));
    }

    @Test
    @DisplayName("Should skip blacklisting already expired token")
    void blacklistToken_WithExpiredToken_SkipsBlacklist() {
        String token = "expired.jwt.token";
        Date pastExpiration = new Date(System.currentTimeMillis() - 3600000);

        when(jwtService.extractExpiration(token)).thenReturn(pastExpiration);

        tokenBlacklistService.blacklistToken(token);

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Should return true when token is blacklisted")
    void isTokenBlacklisted_WithBlacklistedToken_ReturnsTrue() {
        String token = "blacklisted.jwt.token";
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when token is not blacklisted")
    void isTokenBlacklisted_WithNonBlacklistedToken_ReturnsFalse() {
        String token = "clean.jwt.token";
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should treat null Redis response as blacklisted (fail-secure)")
    void isTokenBlacklisted_WhenRedisReturnsNull_ReturnsTrue() {
        String token = "unknown.jwt.token";
        when(redisTemplate.hasKey(anyString())).thenReturn(null);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should treat Redis exception as blacklisted (fail-secure)")
    void isTokenBlacklisted_WhenRedisThrows_ReturnsTrue() {
        String token = "error.jwt.token";
        when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis down"));

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should throw IllegalStateException when Redis is unavailable during blacklist")
    void blacklistToken_WhenRedisUnavailable_ThrowsIllegalState() {
        String token = "unavailable.jwt.token";
        Date futureExpiration = new Date(System.currentTimeMillis() + 3600000);

        when(jwtService.extractExpiration(token)).thenReturn(futureExpiration);
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis connection failed"));

        assertThatThrownBy(() -> tokenBlacklistService.blacklistToken(token))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Token blacklist service unavailable");
    }

    @Test
    @DisplayName("Should use default TTL when expiration cannot be extracted")
    void blacklistToken_WithNullExpiration_UsesDefaultTtl() {
        String token = "no-expiry.jwt.token";

        when(jwtService.extractExpiration(token)).thenReturn(null);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenBlacklistService.blacklistToken(token);

        verify(valueOperations).set(anyString(), eq("1"), eq(Duration.ofHours(24)));
    }
}
