package com.dev.quikkkk.controller;

import com.dev.quikkkk.service.IRedisCacheStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/cache")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class CacheManagementController {
    private final IRedisCacheStatisticsService cacheStatistics;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        return ResponseEntity.ok(cacheStatistics.getCacheStats());
    }

    @GetMapping("/keys")
    public ResponseEntity<Map<String, Long>> getCacheKeys() {
        return ResponseEntity.ok(cacheStatistics.getCacheKeyCount());
    }
    
    @DeleteMapping("/clear/{cache-name}")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable("cache-name") String cacheName) {
        cacheStatistics.clearCache(cacheName);
        return ResponseEntity.ok(Map.of(
                "message", "Cache cleared: " + cacheName,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        log.warn("Clearing all caches - requested by admin");
        cacheStatistics.clearAllCaches();
        return ResponseEntity.ok(Map.of(
                "message", "All caches cleared",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
