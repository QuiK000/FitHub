package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.enums.JwtTokenType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.service.IJwtService;
import com.dev.quikkkk.utils.KeyUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.dev.quikkkk.enums.ErrorCode.TOKEN_INVALID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements IJwtService {
    private static final String TOKEN_TYPE = "token_type";
    private static final String USER_ID = "user_id";
    private static final String ROLES = "roles";
    private static final String PATH_TO_PRIVATE_KEY = "keys/local-only/private_key.pem";
    private static final String PATH_TO_PUBLIC_KEY = "keys/local-only/public_key.pem";
    private static final PrivateKey PRIVATE_KEY;
    private static final PublicKey PUBLIC_KEY;

    private final Cache<@NonNull String, Claims> claimsCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    static {
        try {
            PRIVATE_KEY = KeyUtils.loadPrivateKey(PATH_TO_PRIVATE_KEY);
            PUBLIC_KEY = KeyUtils.loadPublicKey(PATH_TO_PUBLIC_KEY);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JWT Keys", e);
        }
    }

    @Value("${app.security.jwt.access-token-expiration:860000}")
    private long accessTokenExpiration;

    @Value("${app.security.jwt.refresh-token-expiration:860000}")
    private long refreshTokenExpiration;

    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> claims = Map.of(
                TOKEN_TYPE, JwtTokenType.ACCESS.name(),
                USER_ID, user.getId(),
                ROLES, user.getRoles().stream().map(Role::getName).toList()
        );

        return buildToken(user.getEmail(), claims, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = Map.of(
                TOKEN_TYPE, JwtTokenType.REFRESH.name(),
                USER_ID, user.getId()
        );

        return buildToken(user.getEmail(), claims, refreshTokenExpiration);
    }

    @Override
    public String extractEmail(String token) {
        return getCachedClaims(token).getSubject();
    }

    @Override
    public String extractUserId(String token) {
        return getCachedClaims(token).get(USER_ID, String.class);
    }

    @Override
    public String extractTokenType(String token) {
        String type = getCachedClaims(token).get(TOKEN_TYPE, String.class);
        if (type == null) throw new BusinessException(TOKEN_INVALID);
        return type;
    }

    @Override
    public List<String> extractRoles(String token) {
        Object raw = getCachedClaims(token).get(ROLES);

        if (raw == null) return List.of();
        if (!(raw instanceof List<?> list)) throw new BusinessException(TOKEN_INVALID);

        return list.stream()
                .map(String::valueOf)
                .toList();
    }

    @Override
    public Date extractExpiration(String token) {
        return getCachedClaims(token).getExpiration();
    }

    private String buildToken(String email, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(PRIVATE_KEY)
                .compact();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return getCachedClaims(token)
                .getExpiration()
                .before(new Date());
    }

    @Override
    public boolean isRefreshToken(String token) {
        return JwtTokenType.REFRESH.name().equals(extractTokenType(token));
    }

    private Claims getCachedClaims(String token) {
        return claimsCache.get(token, this::extractClaimsInternal);
    }

    private Claims extractClaimsInternal(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(PUBLIC_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new BusinessException(TOKEN_INVALID);
        }
    }
}
