package com.dev.quikkkk.modules.notification.service.impl;

import com.dev.quikkkk.modules.notification.service.ITelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationServiceImpl implements ITelegramNotificationService {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    @Override
    public void sendNotification(String chatId, String message) {
        if (chatId == null || chatId.trim().isEmpty()) {
            log.warn("Telegram chat ID is empty. Cannot send message: {}", message);
            return;
        }

        String url = apiUrl + botToken + "/sendMessage";
        Map<String, String> requestBody = Map.of(
                "chat_id", chatId,
                "text", message,
                "parse_mode", "HTML"
        );

        try {
            restTemplate.postForEntity(url, requestBody, String.class);
            log.info("Telegram notification sent successfully to chat {}", chatId);
        } catch (Exception e) {
            log.error("Failed to send notification to chat {}: {}", chatId, e.getMessage());
        }
    }
}
