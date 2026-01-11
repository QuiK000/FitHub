package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.service.IRedisCacheStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheStatisticsServiceImpl implements IRedisCacheStatisticsService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            Properties info = Objects.requireNonNull(redisTemplate.getConnectionFactory())
                    .getConnection()
                    .serverCommands()
                    .info();

            stats.put("used_memory", info.getProperty("used_memory_human"));
            stats.put("used_memory_peak", info.getProperty("used_memory_peak_human"));
            stats.put("mem_fragmentation_ratio", info.getProperty("mem_fragmentation_ratio"));

            Long dbSize = redisTemplate.getConnectionFactory().getConnection().serverCommands().dbSize();

            stats.put("total_keys", dbSize);
            stats.put("keyspace_hits", info.getProperty("keyspace_hits"));
            stats.put("keyspace_misses", info.getProperty("keyspace_misses"));

            long hits = Long.parseLong(info.getProperty("keyspace_hits", "0"));
            long misses = Long.parseLong(info.getProperty("keyspace_misses", "0"));
            double hitRate = hits + misses > 0 ? (double) hits / (hits + misses) * 100 : 0;

            stats.put("hit_rate_percentage", String.format("%.2f%%", hitRate));
            stats.put("connected_clients", info.getProperty("connected_clients"));
            stats.put("blocked_clients", info.getProperty("blocked_clients"));
            stats.put("evicted_keys", info.getProperty("evicted_keys"));
            stats.put("expired_keys", info.getProperty("expired_keys"));
        } catch (Exception e) {
            log.error("Failed to get Redis stats", e);
            stats.put("error", e.getMessage());
        }

        return stats;
    }

    @Override
    public Map<String, Long> getCacheKeyCount() {
        Map<String, Long> keyCounts = new HashMap<>();
        String[] patterns = {
                "users:*",
                "clientProfiles:*",
                "trainerProfiles:*",
                "specializations:*",
                "trainingSessions:*",
                "memberships:*",
                "attendance:*",
                "lists:*",
                "blacklist_token_*",
                "rate_limit:*",
                "session:lock:*"
        };

        for (String pattern : patterns) keyCounts.put(pattern, scanCount(pattern));
        return keyCounts;
    }

    @Override
    public void clearCache(String cacheName) {
        String pattern = cacheName + ":*";

        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared {} keys for cache: {}", keys.size(), cacheName);
            }
        } catch (Exception e) {
            log.error("Failed to clear cache: {}", cacheName, e);
        }
    }

    @Override
    public void clearAllCaches() {
        try {
            Objects.requireNonNull(redisTemplate.getConnectionFactory())
                    .getConnection()
                    .serverCommands()
                    .flushDb();
            log.info("All caches cleared");
        } catch (Exception e) {
            log.error("Failed to clear all caches", e);
        }
    }

    private long scanCount(String pattern) {
        long count = 0;

        try (var connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()) {
            try (var cursor = connection.keyCommands().scan(
                    ScanOptions.scanOptions()
                            .match(pattern)
                            .count(1000)
                            .build())) {
                while (cursor.hasNext()) {
                    cursor.next();
                    count++;

                }
            }
        } catch (Exception e) {
            log.error("Failed to scan keys for pattern {}", pattern, e);
            return -1;
        }

        return count;
    }
}
