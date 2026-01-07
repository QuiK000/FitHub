package com.dev.quikkkk.service;

public interface ITokenBlacklistService {
    void blacklistToken(String token);

    boolean isTokenBlacklisted(String token);

    void cleanupExpiredTokens();
}
