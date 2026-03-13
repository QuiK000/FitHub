package com.dev.quikkkk.modules.auth.service;

public interface ITokenBlacklistService {
    void blacklistToken(String token);

    boolean isTokenBlacklisted(String token);

    void cleanupExpiredTokens();
}
