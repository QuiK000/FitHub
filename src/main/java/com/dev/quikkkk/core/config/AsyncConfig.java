package com.dev.quikkkk.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {
    @Bean(name = "emailTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);

        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);

        executor.setAllowCoreThreadTimeOut(true);

        executor.setThreadNamePrefix("email-");
        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.setAwaitTerminationSeconds(60);

        executor.setRejectedExecutionHandler((runnable, pool) -> {
            log.warn("Email task rejected, executing in caller thread. Queue size: {}", pool.getQueue().size());
            if (!pool.isShutdown()) {
                runnable.run();
            }
        });

        executor.initialize();
        log.info("Email TaskExecutor initialized with corePoolSize={}, maxPoolSize={}, queueCapacity={}",
                executor.getCorePoolSize(),
                executor.getMaxPoolSize(),
                executor.getQueueCapacity()
        );

        return executor;
    }
}
