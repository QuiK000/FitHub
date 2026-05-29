package com.dev.quikkkk.modules.auth.service.impl;

import com.dev.quikkkk.modules.auth.service.IJwtService;
import com.dev.quikkkk.modules.auth.service.ITokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements ITokenBlacklistService {
    private static final String BLACKLIST_KEY_PREFIX = "blacklist_token_";
    private static final Duration DEFAULT_BLACKLIST_TTL = Duration.ofHours(24);

    private final RedisTemplate<String, Object> redisTemplate;
    private final IJwtService jwtService;

    @Override
    public void blacklistToken(String token) {
        String key = buildKey(token);
        String tokenHint = buildTokenHint(token);

        try {
            Duration ttl = resolveTokenTtl(token);

            if (ttl.isNegative() || ttl.isZero()) {
                log.debug("Token [{}] already expired, skipping blacklist", tokenHint);
                return;
            }

            redisTemplate.opsForValue().set(key, "1", ttl);
            log.debug("Token [{}] blacklisted successfully. TTL={}s", tokenHint, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Failed to blacklist token [{}]", tokenHint, e);
            throw new IllegalStateException("Token blacklist service unavailable", e);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = buildKey(token);
        String tokenHint = buildTokenHint(token);

        try {
            Boolean exists = redisTemplate.hasKey(key);
            if (exists == null) {
                log.warn(
                        "Redis returned null during blacklist check for token [{}]. "
                                + "Treating token as blacklisted (fail-secure)", tokenHint
                );

                return true;
            }

            return exists;
        } catch (Exception e) {
            log.error(
                    "Error checking blacklist status for token [{}]. "
                            + "Treating token as blacklisted (fail-secure)", tokenHint, e
            );

            return true;
        }
    }

    @Override
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanupExpiredTokens() {
        log.debug("Starting blacklist orphan-key cleanup");
        try {
            long removed = cleanupKeysWithoutTtl();
            if (removed > 0) {
                log.info("Blacklist cleanup completed. Removed {} orphan-keys", removed);
            }
        } catch (Exception e) {
            log.error("Blacklist cleanup failed", e);
        }
    }

    private long cleanupKeysWithoutTtl() {
        long deleted = 0;
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();

        if (factory == null) {
            throw new IllegalStateException(
                    "Redis connection factory is not configured"
            );
        }

        try (var connection = factory.getConnection()) {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(BLACKLIST_KEY_PREFIX + "*")
                    .count(500)
                    .build();

            try (var cursor = connection.keyCommands().scan(options)) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    Long ttl = connection.keyCommands().ttl(keyBytes);

                    if (ttl != null && ttl == -1L) {
                        connection.keyCommands().del(keyBytes);
                        deleted++;

                        log.warn("Removed orphan blacklist entry");
                    }
                }
            }
        }

        return deleted;
    }

    private Duration resolveTokenTtl(String token) {
        try {
            Date expiration = jwtService.extractExpiration(token);

            if (expiration == null) {
                log.warn(
                        "Could not determine token expiration. " + "Using default blacklist TTL={}h",
                        DEFAULT_BLACKLIST_TTL.toHours()
                );

                return DEFAULT_BLACKLIST_TTL;
            }

            long ttlMs = expiration.getTime() - System.currentTimeMillis();
            return Duration.ofMillis(ttlMs);
        } catch (Exception e) {
            log.warn(
                    "Failed to extract token expiration. Using default blacklist TTL={}h",
                    DEFAULT_BLACKLIST_TTL.toHours(), e
            );
            return DEFAULT_BLACKLIST_TTL;
        }
    }

    private String buildKey(String token) {
        return BLACKLIST_KEY_PREFIX + DigestUtils.sha256Hex(token);
    }

    private String buildTokenHint(String token) {
        return buildKey(token)
                .replace(BLACKLIST_KEY_PREFIX, "")
                .substring(0, 8);
    }
}
