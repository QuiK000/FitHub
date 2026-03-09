package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.entity.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {
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
                .timeAgo(null) // TODO: FIX
                .build();
    }
}
