package com.dev.quikkkk.utils;

import com.dev.quikkkk.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

import static com.dev.quikkkk.enums.ErrorCode.TOO_MANY_REQUESTS;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redis;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        String userId = SecurityUtils.getCurrentUserId();
        String key = "rate_limit:" + userId;
        Long requests = redis.opsForValue().increment(key);

        if (requests == 1) redis.expire(key, Duration.ofMinutes(1));
        if (requests > 100) throw new BusinessException(TOO_MANY_REQUESTS);
        return true;
    }
}
