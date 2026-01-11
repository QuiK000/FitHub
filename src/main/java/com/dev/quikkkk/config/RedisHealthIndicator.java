package com.dev.quikkkk.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthIndicator implements HealthIndicator {
    private final RedisConnectionFactory connectionFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public @Nullable Health health() {
        try (var connection = connectionFactory.getConnection()) {
            Properties info = connection.serverCommands().info();
            Map<String, Object> details = new HashMap<>();

            details.put("version", info.getProperty("redis_version"));
            details.put("mode", info.getProperty("redis_mode"));
            details.put("connected_clients", info.getProperty("connected_clients"));
            details.put("used_memory_human", info.getProperty("used_memory_human"));
            details.put("uptime_in_days", info.getProperty("uptime_in_days"));

            String testKey = "health:check:" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, "OK");

            String testValue = (String) redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);

            if (!"OK".equals(testValue)) {
                return Health.down()
                        .withDetail("error", "Redis read/write test failed")
                        .build();
            }

            return Health.up().withDetails(details).build();
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return Health.down()
                    .withException(e)
                    .build();
        }
    }
}
