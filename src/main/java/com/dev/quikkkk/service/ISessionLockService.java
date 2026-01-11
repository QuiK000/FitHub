package com.dev.quikkkk.service;

import com.dev.quikkkk.functional.LockOperation;

public interface ISessionLockService {
    String acquireLock(String sessionId);

    void releaseLock(String sessionId, String lockValue);

    <T> T executeWithLock(String sessionId, LockOperation<T> operation);
}
