package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.Notification;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.NotificationMapper;
import com.dev.quikkkk.repository.INotificationRepository;
import com.dev.quikkkk.service.INotificationService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.dev.quikkkk.enums.ErrorCode.NOTIFICATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final MessageMapper messageMapper;

    @Override
    public PageResponse<NotificationResponse> findAllNotifications(int size, int page) {
        String userId = SecurityUtils.getCurrentUserId();
        Pageable notificationPage = PaginationUtils.createPageRequest(size, page, "createdDate");
        Page<Notification> notifications = notificationRepository.findAllByRecipientId(userId, notificationPage);

        return PaginationUtils.toPageResponse(notifications, notificationMapper::toResponse);
    }

    @Override
    public NotificationResponse findNotificationById(String id) {
        String userId = SecurityUtils.getCurrentUserId();
        return notificationRepository.findByIdAndRecipientId(id, userId)
                .map(notificationMapper::toResponse)
                .orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND));
    }

    @Override
    @Transactional
    public MessageResponse readNotification(String id) {
        String userId = SecurityUtils.getCurrentUserId();
        Notification notification = notificationRepository.findByIdAndRecipientId(id, userId)
                .orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        return messageMapper.message("Notification marked as read");
    }

    @Override
    @Transactional
    public MessageResponse markAllRead() {
        String userId = SecurityUtils.getCurrentUserId();
        int updated = notificationRepository.markAllAsReadByRecipientId(userId, LocalDateTime.now());

        return messageMapper.message(updated + " notifications marked as read");
    }

    @Override
    public long getUnreadCount() {
        String userId = SecurityUtils.getCurrentUserId();
        return notificationRepository.countAllByRecipientIdAndReadIsFalse(userId);
    }

    @Override
    public NotificationSummaryResponse getNotificationSummary() {
        String userId = SecurityUtils.getCurrentUserId();
        Long getUnreadCount = getUnreadCount();
        List<Notification> notifications = notificationRepository.findTop5ByRecipientIdOrderByCreatedDateDesc(userId);

        return notificationMapper.toResponseSummary(getUnreadCount, notifications);
    }
}
