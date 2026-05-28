package com.dev.quikkkk.core.utils;

import com.dev.quikkkk.core.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

import static com.dev.quikkkk.core.enums.ErrorCode.TOO_MANY_REQUESTS;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final String KEY_PREFIX = "rate_limit:api:";

    private final StringRedisTemplate redis;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        String identifier = resolveIdentifier(request);
        String key = KEY_PREFIX + identifier;
        Long requests = redis.opsForValue().increment(key);

        if (requests != null && requests.equals(1L)) redis.expire(key, Duration.ofMinutes(1));
        if (requests != null && requests > MAX_REQUESTS_PER_MINUTE) {
            log.warn(
                    "Rate limit exceeded for identifier={}, path={}, count={}",
                    identifier,
                    request.getRequestURI(),
                    requests
            );

            throw new BusinessException(TOO_MANY_REQUESTS);
        }

        return true;
    }

    private String resolveIdentifier(HttpServletRequest request) {
        String userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId != null) return "user: " + userId;
        return "ip:" + ClientIpUtils.getClientIpAddress(request);
    }
}
