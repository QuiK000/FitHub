package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.entity.Notification;
import com.dev.quikkkk.utils.TimeAgoFormatter;
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
}
