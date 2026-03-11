package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.dto.response.PageResponse;

public interface INotificationService {
    PageResponse<NotificationResponse> findAllNotifications(int size, int page);

    NotificationResponse findNotificationById(String id);

    MessageResponse readNotification(String id);

    MessageResponse markAllRead();

    long getUnreadCount();

    NotificationSummaryResponse getNotificationSummary();
}
