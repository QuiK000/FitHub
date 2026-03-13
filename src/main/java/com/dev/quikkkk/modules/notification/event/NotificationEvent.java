package com.dev.quikkkk.modules.notification.event;

import com.dev.quikkkk.modules.notification.enums.NotificationPriority;
import com.dev.quikkkk.modules.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationEvent {
    private final String recipientId;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final NotificationPriority priority;
    private final String actionUrl;
    private final String referenceId;
    private final String referenceType;
    private final LocalDateTime scheduledFor;
}
