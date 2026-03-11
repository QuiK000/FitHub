package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.Notification;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.event.NotificationEvent;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.NotificationMapper;
import com.dev.quikkkk.repository.INotificationRepository;
import com.dev.quikkkk.repository.IUserRepository;
import com.dev.quikkkk.service.INotificationService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dev.quikkkk.enums.ErrorCode.NOTIFICATION_NOT_FOUND;

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

        notificationRepository.save(notification);
        incrementUnreadCountInCache(event.getRecipientId());
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
