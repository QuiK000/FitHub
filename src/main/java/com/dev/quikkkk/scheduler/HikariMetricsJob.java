package com.dev.quikkkk.scheduler;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HikariMetricsJob {
    private final HikariDataSource dataSource;

    @Scheduled(fixedRate = 60000)
    public void logPoolStats() {
        HikariPoolMXBean pool = dataSource.getHikariPoolMXBean();
        log.info(
                "DB Pool - Active: {}, Idle: {}, Total: {}, Warning: {}",
                pool.getActiveConnections(),
                pool.getIdleConnections(),
                pool.getTotalConnections(),
                pool.getThreadsAwaitingConnection()
        );
    }
}
