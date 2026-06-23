package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.functional.LockOperation;
import com.dev.quikkkk.modules.auth.service.impl.SessionLockServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionLockService Tests")
class SessionLockServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private SessionLockServiceImpl sessionLockService;

    @Test
    @DisplayName("Should acquire lock successfully")
    void acquireLock_WhenLockAvailable_ReturnsLockValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);

        String lockValue = sessionLockService.acquireLock("session-123");

        assertThat(lockValue).isNotNull();
        verify(valueOperations).setIfAbsent(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should return null when lock is not available")
    void acquireLock_WhenLockNotAvailable_ReturnsNull() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any())).thenReturn(false);

        String lockValue = sessionLockService.acquireLock("session-123");

        assertThat(lockValue).isNull();
    }

    @Test
    @DisplayName("Should release lock when lock value matches")
    void releaseLock_WithMatchingValue_DeletesLock() {
        String sessionId = "session-123";
        String lockValue = "lock-value-123";
        String lockKey = "session:lock:" + sessionId;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(lockKey)).thenReturn(lockValue);

        sessionLockService.releaseLock(sessionId, lockValue);

        verify(redisTemplate).delete(lockKey);
    }

    @Test
    @DisplayName("Should not release lock when lock value does not match")
    void releaseLock_WithNonMatchingValue_DoesNotDelete() {
        String sessionId = "session-123";
        String lockValue = "lock-value-123";
        String differentValue = "lock-value-456";
        String lockKey = "session:lock:" + sessionId;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(lockKey)).thenReturn(differentValue);

        sessionLockService.releaseLock(sessionId, lockValue);

        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("Should skip release when lock value is null")
    void releaseLock_WithNullValue_DoesNothing() {
        sessionLockService.releaseLock("session-123", null);

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should execute operation within lock")
    void executeWithLock_WhenLockAvailable_ExecutesOperation() {
        String sessionId = "session-123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any())).thenReturn(true);

        LockOperation<String> operation = () -> "result";
        String result = sessionLockService.executeWithLock(sessionId, operation);

        assertThat(result).isEqualTo("result");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw BusinessException when lock cannot be acquired")
    void executeWithLock_WhenLockNotAvailable_ThrowsBusinessException() {
        String sessionId = "session-123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any())).thenReturn(false);

        LockOperation<String> operation = () -> "result";

        assertThatThrownBy(() -> sessionLockService.executeWithLock(sessionId, operation))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SESSION_IS_FULL);
    }
}
