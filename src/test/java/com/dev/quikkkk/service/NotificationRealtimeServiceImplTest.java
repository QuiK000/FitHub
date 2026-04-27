package com.dev.quikkkk.service;

import com.dev.quikkkk.modules.notification.dto.response.NotificationRealtimeEventResponse;
import com.dev.quikkkk.modules.notification.realtime.impl.NotificationRealtimeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("NotificationRealtimeService Tests")
public class NotificationRealtimeServiceImplTest {
    @Test
    @DisplayName("Should subscribe and publish realtime event without exceptions")
    void subscribeAndPublish_ShouldWork() {
        NotificationRealtimeServiceImpl service = new NotificationRealtimeServiceImpl();
        SseEmitter emitter = service.subscribe("user-1");

        NotificationRealtimeEventResponse event = NotificationRealtimeEventResponse.builder()
                .eventType("notification.created")
                .unreadCount(1)
                .timestamp(LocalDateTime.now())
                .build();

        assertThat(emitter).isNotNull();
        assertThatCode(() -> service.publishToUser("user-1", event)).doesNotThrowAnyException();
    }
}
