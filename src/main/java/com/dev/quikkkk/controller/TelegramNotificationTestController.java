package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.service.ITelegramNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/test-telegram")
@RequiredArgsConstructor
public class TelegramNotificationTestController {
    private final ITelegramNotificationService notificationService;
    private final MessageMapper messageMapper;

    @PostMapping("/test-notification")
    public ResponseEntity<MessageResponse> testNotification() {
        String message = String.format(
                "<b> Новая запись!</b>\nК вам записался клиент: %s\nДата: %s",
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        notificationService.sendNotification("chat_id", message);
        return ResponseEntity.ok(messageMapper.message("Успешно!"));
    }
}
