package com.dev.quikkkk.functional;

@FunctionalInterface
public interface LockOperation<T> {
    T execute();
}
