package com.dev.quikkkk.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceMonitor {
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object monitorPerformance(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            if (duration > 1000) {
                log.warn("Slow requests: {} took {}ms", pjp.getSignature().getName(), duration);
            }
        }
    }
}
