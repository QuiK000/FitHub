package com.dev.quikkkk.scheduler;

import com.dev.quikkkk.repository.INotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupJob {
    private final INotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldNotifications() {
        log.info("Starting cleanup of old read notifications...");

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByReadTrueAndCreatedDateBefore(thirtyDaysAgo);

        log.info("Finished cleanup of old read notifications.");
    }
}
