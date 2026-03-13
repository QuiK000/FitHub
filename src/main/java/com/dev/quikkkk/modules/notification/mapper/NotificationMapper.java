package com.dev.quikkkk.modules.notification.mapper;

import com.dev.quikkkk.modules.notification.dto.response.NotificationResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.modules.notification.entity.Notification;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.notification.event.NotificationEvent;
import com.dev.quikkkk.core.utils.TimeAgoFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationMapper {
    private final TimeAgoFormatter timeAgoFormatter;

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .priority(notification.getPriority())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .readAt(notification.getReadAt())
                .actionUrl(notification.getActionUrl())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .createdAt(notification.getCreatedDate())
                .timeAgo(timeAgoFormatter.format(notification.getCreatedDate()))
                .build();
    }

    public NotificationSummaryResponse toResponseSummary(Long getUnreadCount, List<Notification> notifications) {
        List<NotificationResponse> recentNotifications = notifications.stream().map(this::toResponse).toList();

        return NotificationSummaryResponse.builder()
                .unreadCount(getUnreadCount)
                .recentNotifications(recentNotifications)
                .build();
    }

    public Notification toEvent(User recipientProxy, NotificationEvent event) {
        return Notification.builder()
                .recipient(recipientProxy)
                .title(event.getTitle())
                .message(event.getMessage())
                .type(event.getType())
                .priority(event.getPriority())
                .actionUrl(event.getActionUrl())
                .referenceId(event.getReferenceId())
                .referenceType(event.getReferenceType())
                .read(false)
                .scheduledFor(event.getScheduledFor())
                .build();
    }
}
