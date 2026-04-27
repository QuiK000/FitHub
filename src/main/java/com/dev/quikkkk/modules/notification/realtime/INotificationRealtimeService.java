package com.dev.quikkkk.modules.notification.realtime;

import com.dev.quikkkk.modules.notification.dto.response.NotificationRealtimeEventResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface INotificationRealtimeService {
    SseEmitter subscribe(String userId);

    void publishToUser(String userId, NotificationRealtimeEventResponse event);
}
