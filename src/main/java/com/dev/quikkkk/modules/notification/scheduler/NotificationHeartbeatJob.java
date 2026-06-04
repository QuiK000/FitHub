package com.dev.quikkkk.modules.notification.scheduler;

import com.dev.quikkkk.modules.notification.realtime.INotificationRealtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationHeartbeatJob {
    private static final long HEARTBEAT_INTERVAL_MS = 30_000L;

    private final INotificationRealtimeService realtimeService;

    @Scheduled(fixedRate = HEARTBEAT_INTERVAL_MS)
    public void heartbeat() {
        realtimeService.sendHeartbeat();
    }
}
