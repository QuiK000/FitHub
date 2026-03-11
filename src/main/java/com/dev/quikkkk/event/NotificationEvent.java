package com.dev.quikkkk.event;

import com.dev.quikkkk.enums.NotificationPriority;
import com.dev.quikkkk.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

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
}
