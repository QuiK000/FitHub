package com.dev.quikkkk.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthIndicator implements HealthIndicator {
    private final RedisConnectionFactory connectionFactory;

    @Override
    public @Nullable Health health() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            String pingResponse = connection.ping();
            if (!"PONG".equalsIgnoreCase(pingResponse)) {
                return Health.down()
                        .withDetail("error", "Redis PING failed. Response: " + pingResponse)
                        .build();
            }

            Properties info = connection.serverCommands().info();
            Map<String, Object> details = new HashMap<>();

            if (info != null) {
                addIfPresent(details, "version", info, "redis_version");
                addIfPresent(details, "mode", info, "redis_mode");
                addIfPresent(details, "connected_clients", info, "connected_clients");
                addIfPresent(details, "used_memory_human", info, "used_memory_human");
                addIfPresent(details, "uptime_in_days", info, "uptime_in_days");
            }

            return Health.up().withDetails(details).build();
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return Health.down().withException(e).build();
        }
    }

    private void addIfPresent(Map<String, Object> map, String key, Properties props, String propKey) {
        String value = props.getProperty(propKey);
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }
}
