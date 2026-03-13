package com.dev.quikkkk.modules.notification.controller;

import com.dev.quikkkk.modules.notification.dto.request.BroadcastNotificationRequest;
import com.dev.quikkkk.modules.notification.dto.request.CreateNotificationRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.modules.notification.service.INotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> sendNotification(@Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> broadcastNotification(
            @Valid @RequestBody BroadcastNotificationRequest request
    ) {
        return ResponseEntity.ok(notificationService.broadcastNotification(request));
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
