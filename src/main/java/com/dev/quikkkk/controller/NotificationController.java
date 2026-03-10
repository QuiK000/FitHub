package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.response.NotificationResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;

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
}
