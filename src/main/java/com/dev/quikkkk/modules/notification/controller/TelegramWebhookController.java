package com.dev.quikkkk.modules.notification.controller;

import com.dev.quikkkk.modules.notification.dto.telegram.TelegramUpdate;
import com.dev.quikkkk.modules.notification.service.ITelegramWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramWebhookController {
    private final ITelegramWebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> onWebhookUpdate(@RequestBody TelegramUpdate update) {
        webhookService.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
