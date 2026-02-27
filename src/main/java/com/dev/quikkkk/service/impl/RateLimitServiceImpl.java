package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.service.IRateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.dev.quikkkk.enums.ErrorCode.RESEND_TOO_FREQUENT;
import static com.dev.quikkkk.enums.ErrorCode.TOO_MANY_REQUESTS;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements IRateLimitService {
    private static final String KEY_PREFIX = "rate_limit:email_codes:";
    private static final String LOGIN_ATTEMPT_PREFIX = "rate_limit:login_attempts:";

    private static final long TTL_MINUTES = 1;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;

    private final StringRedisTemplate redisTemplate;

    @Override
    public void checkResendVerificationLimit(String email) {
        String key = KEY_PREFIX + email.toLowerCase();
        Boolean exists = redisTemplate.hasKey(key);

        if (Boolean.TRUE.equals(exists)) throw new BusinessException(RESEND_TOO_FREQUENT);
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(TTL_MINUTES));
    }

    @Override
    public void checkForgotPasswordLimit(String email) {
        String key = KEY_PREFIX + email.toLowerCase();
        Boolean exists = redisTemplate.hasKey(key);

        if (Boolean.TRUE.equals(exists)) throw new BusinessException(RESEND_TOO_FREQUENT);
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(TTL_MINUTES));
    }

    @Override
    public void checkLoginAttempts(String ipAddress) {
        String key = LOGIN_ATTEMPT_PREFIX + ipAddress;
        String attempts = redisTemplate.opsForValue().get(key);

        if (attempts != null && Integer.parseInt(attempts) >= MAX_LOGIN_ATTEMPTS) {
            throw new BusinessException(TOO_MANY_REQUESTS);
        }
    }

    @Override
    public void incrementLoginAttempts(String ipAddress) {
        String key = LOGIN_ATTEMPT_PREFIX + ipAddress;
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(BLOCK_DURATION_MINUTES));
        }
    }

    @Override
    public void resetLoginAttempts(String ipAddress) {
        String key = LOGIN_ATTEMPT_PREFIX + ipAddress;
        redisTemplate.delete(key);
    }
}
