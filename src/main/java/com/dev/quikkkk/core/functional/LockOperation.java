package com.dev.quikkkk.core.functional;

@FunctionalInterface
public interface LockOperation<T> {
    T execute();
}
