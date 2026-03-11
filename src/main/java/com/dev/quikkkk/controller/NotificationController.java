package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.enums.NotificationType;
import com.dev.quikkkk.event.NotificationEvent;
import com.dev.quikkkk.service.INotificationService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;
    private final ApplicationEventPublisher publisher;

    // TODO: TEST
    @PostMapping("/test-event")
    public ResponseEntity<String> fireTestEvent() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        NotificationEvent event = NotificationEvent.builder()
                .recipientId(currentUserId)
                .title("Тестова подія!")
                .message("Перевіряємо чи працює Redis та Spring Events")
                .type(NotificationType.GENERAL_ANNOUNCEMENT)
                .build();

        publisher.publishEvent(event);
        return ResponseEntity.ok("Подію успішно відправлено в шину spring");
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(notificationService.findAllNotifications(page, size));
    }

    @GetMapping("/{notification-id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable("notification-id") String notificationId) {
        return ResponseEntity.ok(notificationService.findNotificationById(notificationId));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }

    @GetMapping("/summary")
    public ResponseEntity<NotificationSummaryResponse> getNotificationSummary() {
        return ResponseEntity.ok(notificationService.getNotificationSummary());
    }

    @PatchMapping("/{notification-id}/read")
    public ResponseEntity<MessageResponse> readNotification(@PathVariable("notification-id") String notificationId) {
        return ResponseEntity.ok(notificationService.readNotification(notificationId));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<MessageResponse> markAllRead() {
        return ResponseEntity.ok(notificationService.markAllRead());
    }
}
