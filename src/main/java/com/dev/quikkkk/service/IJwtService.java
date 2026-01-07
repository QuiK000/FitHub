package com.dev.quikkkk.service;

import com.dev.quikkkk.entity.User;

import java.util.Date;
import java.util.List;

public interface IJwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String refreshAccessToken(String refreshToken);

    String extractEmail(String token);

    String extractUserId(String token);

    String extractTokenType(String token);

    List<String> extractRoles(String token);

    Date extractExpiration(String token);

    boolean isTokenValid(String token, String expectedEmail);

    boolean isTokenExpired(String token);
}
