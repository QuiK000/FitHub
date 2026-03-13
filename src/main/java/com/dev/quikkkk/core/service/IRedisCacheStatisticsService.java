package com.dev.quikkkk.core.service;

import java.util.Map;

public interface IRedisCacheStatisticsService {
    Map<String, Object> getCacheStats();

    Map<String, Long> getCacheKeyCount();

    void clearCache(String cacheName);

    void clearAllCaches();
}
