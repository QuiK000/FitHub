package com.dev.quikkkk.utils;

import com.dev.quikkkk.service.ISpecializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmer {
    private final ISpecializationService specializationService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmupCaches() {
        log.info("Warming up caches...");
        specializationService.getAllActive(0, 100, null);
    }
}
