package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.service.IRateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.dev.quikkkk.enums.ErrorCode.RESEND_TOO_FREQUENT;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements IRateLimitService {
    private static final String KEY_PREFIX = "rate_limit:email_codes:";
    private static final long TTL_MINUTES = 1;

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
}
