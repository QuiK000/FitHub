package com.dev.quikkkk.modules.notification.realtime.impl;

import com.dev.quikkkk.modules.notification.dto.response.NotificationRealtimeEventResponse;
import com.dev.quikkkk.modules.notification.realtime.INotificationRealtimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class NotificationRealtimeServiceImpl implements INotificationRealtimeService {
    private static final long SSE_TIMEOUT_MS = Duration.ofMinutes(30).toMillis();
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emittersByUser.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> {
            removeEmitter(userId, emitter);
            emitter.complete();
        });

        emitter.onError(throwable -> {
            log.debug("SSE emitter error for user {}: {}", userId, throwable.getMessage());
            removeEmitter(userId, emitter);
        });

        sendSafely(userId, emitter, NotificationRealtimeEventResponse.builder()
                .eventType("notification.connected")
                .unreadCount(0)
                .timestamp(LocalDateTime.now())
                .build());

        return emitter;
    }

    @Override
    public void publishToUser(String userId, NotificationRealtimeEventResponse event) {
        List<SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null || emitters.isEmpty()) return;

        for (SseEmitter emitter : emitters) sendSafely(userId, emitter, event);
    }

    private void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null) return;

        emitters.remove(emitter);
        if (emitters.isEmpty()) emittersByUser.remove(userId);
    }

    private void sendSafely(String userId, SseEmitter emitter, NotificationRealtimeEventResponse event) {
        try {
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(event.getEventType())
                    .data(event));
        } catch (IOException ex) {
            log.debug("Failed to send SSE event to user {}: {}", userId, ex.getMessage());
            removeEmitter(userId, emitter);
            emitter.completeWithError(ex);
        }
    }
}
