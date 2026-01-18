package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.functional.LockOperation;
import com.dev.quikkkk.service.ISessionLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static com.dev.quikkkk.enums.ErrorCode.SESSION_IS_FULL;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionLockServiceImpl implements ISessionLockService {
    private static final String LOCK_PREFIX = "session:lock:";
    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);

    private final StringRedisTemplate redisTemplate;

    @Override
    public String acquireLock(String sessionId) {
        String lockKey = LOCK_PREFIX + sessionId;
        String lockValue = UUID.randomUUID().toString();
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

        if (Boolean.TRUE.equals(acquired)) {
            log.debug("Lock acquired for session {}", sessionId);
            return lockValue;
        }

        log.debug("Failed to acquired lock for session: {}", sessionId);
        return null;
    }

    @Override
    public void releaseLock(String sessionId, String lockValue) {
        if (lockValue == null) return;

        String lockKey = LOCK_PREFIX + sessionId;
        String currentValue = redisTemplate.opsForValue().get(lockKey);

        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
            log.debug("Lock released for session {}", sessionId);
        }
    }

    @Override
    public <T> T executeWithLock(String sessionId, LockOperation<T> operation) {
        String lockValue = acquireLock(sessionId);
        try {
            lockValue = acquireLockWithRetry(sessionId, 3, Duration.ofMillis(100));
            if (lockValue == null) throw new BusinessException(SESSION_IS_FULL);
            return operation.execute();
        } finally {
            if (lockValue != null) {
                releaseLock(sessionId, lockValue);
            }
        }
    }

    private String acquireLockWithRetry(String sessionId, int maxRetries, Duration delay) {
        for (int i = 0; i < maxRetries; i++) {
            String lock = acquireLock(sessionId);
            if (lock != null) return lock;
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                break;
            }
        }

        return null;
    }
}
