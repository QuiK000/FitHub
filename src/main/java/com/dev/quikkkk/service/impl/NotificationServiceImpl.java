package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.Notification;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public PageResponse<NotificationResponse> findAllNotifications(int size, int page) {
        String userId = SecurityUtils.getCurrentUserId();
        Pageable notificationPage = PaginationUtils.createPageRequest(size, page, "createdDate");
        Page<Notification> notifications = notificationRepository.findNotificationsByRecipientId(userId, notificationPage);

        return PaginationUtils.toPageResponse(notifications, notificationMapper::toResponse);
    }
}
