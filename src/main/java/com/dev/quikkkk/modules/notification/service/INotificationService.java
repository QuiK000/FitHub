package com.dev.quikkkk.modules.notification.service;

import com.dev.quikkkk.modules.notification.dto.request.BroadcastNotificationRequest;
import com.dev.quikkkk.modules.notification.dto.request.CreateNotificationRequest;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.notification.event.NotificationEvent;

public interface INotificationService {
    PageResponse<NotificationResponse> findAllNotifications(int size, int page);

    NotificationResponse findNotificationById(String id);

    MessageResponse readNotification(String id);

    MessageResponse markAllRead();

    long getUnreadCount();

    NotificationSummaryResponse getNotificationSummary();

    void createNotificationFromEvent(NotificationEvent event);

    MessageResponse sendNotification(CreateNotificationRequest request);

    MessageResponse broadcastNotification(BroadcastNotificationRequest request);
}
