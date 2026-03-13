package com.dev.quikkkk.modules.notification.service.impl;

import com.dev.quikkkk.modules.notification.dto.request.BroadcastNotificationRequest;
import com.dev.quikkkk.modules.notification.dto.request.CreateNotificationRequest;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.notification.entity.Notification;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.notification.event.NotificationEvent;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.modules.notification.mapper.NotificationMapper;
import com.dev.quikkkk.modules.notification.repository.INotificationRepository;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
import com.dev.quikkkk.modules.notification.service.INotificationService;
import com.dev.quikkkk.core.utils.PaginationUtils;
import com.dev.quikkkk.core.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dev.quikkkk.core.enums.ErrorCode.NOTIFICATION_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationServiceImpl implements INotificationService {
    private static final String UNREAD_COUNT_KEY = "notification:unread:user:";

    private final INotificationRepository notificationRepository;
    private final IUserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final MessageMapper messageMapper;
    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher publisher;

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
            decrementUnreadCountInCache(userId);
        }

        return messageMapper.message("Notification marked as read");
    }

    @Override
    @Transactional
    public MessageResponse markAllRead() {
        String userId = SecurityUtils.getCurrentUserId();
        int updated = notificationRepository.markAllAsReadByRecipientId(userId, LocalDateTime.now());

        if (updated > 0) redisTemplate.opsForValue().set(UNREAD_COUNT_KEY + userId, "0");
        return messageMapper.message(updated + " notifications marked as read");
    }

    @Override
    public long getUnreadCount() {
        String userId = SecurityUtils.getCurrentUserId();
        return getUnreadCountForUser(userId);
    }

    @Override
    public NotificationSummaryResponse getNotificationSummary() {
        String userId = SecurityUtils.getCurrentUserId();
        Long getUnreadCount = getUnreadCount();
        List<Notification> notifications = notificationRepository.findTop5ByRecipientIdOrderByCreatedDateDesc(userId);

        return notificationMapper.toResponseSummary(getUnreadCount, notifications);
    }

    @Override
    @Transactional
    public void createNotificationFromEvent(NotificationEvent event) {
        User recipientProxy = userRepository.getReferenceById(event.getRecipientId());
        Notification notification = notificationMapper.toEvent(recipientProxy, event);
        boolean isScheduledForFuture = event.getScheduledFor() != null
                && event.getScheduledFor().isAfter(LocalDateTime.now());

        notification.setSent(!isScheduledForFuture);
        notificationRepository.save(notification);

        if (!isScheduledForFuture) incrementUnreadCountInCache(event.getRecipientId());
    }

    @Override
    public MessageResponse sendNotification(CreateNotificationRequest request) {
        if (!userRepository.existsById(request.getRecipientId())) throw new BusinessException(USER_NOT_FOUND);

        NotificationEvent event = NotificationEvent.builder()
                .recipientId(request.getRecipientId())
                .type(request.getType())
                .priority(request.getPriority())
                .title(request.getTitle())
                .message(request.getMessage())
                .actionUrl(request.getActionUrl())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .scheduledFor(request.getScheduledFor())
                .build();

        publisher.publishEvent(event);
        return messageMapper.message("Notification dispatched successfully");
    }

    @Override
    public MessageResponse broadcastNotification(BroadcastNotificationRequest request) {
        List<String> userIds = userRepository.findAllUserIds();

        for (String userId : userIds) {
            NotificationEvent event = NotificationEvent.builder()
                    .recipientId(userId)
                    .type(request.getType())
                    .priority(request.getPriority())
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .actionUrl(request.getActionUrl())
                    .referenceId(request.getReferenceId())
                    .referenceType(request.getReferenceType())
                    .scheduledFor(request.getScheduledFor())
                    .build();

            publisher.publishEvent(event);
        }

        return messageMapper.message("Broadcast initiated for " + userIds.size() + " users.");
    }

    private long getUnreadCountForUser(String userId) {
        String key = UNREAD_COUNT_KEY + userId;
        String cachedCount = redisTemplate.opsForValue().get(key);

        if (cachedCount != null) return Long.parseLong(cachedCount);
        long countFromDb = notificationRepository.countAllByRecipientIdAndReadIsFalse(userId);

        redisTemplate.opsForValue().set(key, String.valueOf(countFromDb), 7, TimeUnit.DAYS);
        return countFromDb;
    }

    private void incrementUnreadCountInCache(String userId) {
        String key = UNREAD_COUNT_KEY + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().increment(key);
        } else {
            getUnreadCountForUser(userId);
        }
    }

    private void decrementUnreadCountInCache(String userId) {
        String key = UNREAD_COUNT_KEY + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long current = redisTemplate.opsForValue().decrement(key);
            if (current != null && current < 0) {
                redisTemplate.opsForValue().set(key, "0");
            }
        }
    }
}
