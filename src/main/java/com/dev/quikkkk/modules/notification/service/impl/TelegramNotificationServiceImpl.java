package com.dev.quikkkk.modules.notification.service.impl;

import com.dev.quikkkk.modules.notification.service.ITelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
    @Retryable(
            retryFor = { Exception.class },
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendNotification(String chatId, String message) {
        if (chatId == null || chatId.trim().isEmpty()) {
            log.warn("Telegram chat ID is empty. Cannot send message: {}", message);
            return;
        }

        String url = apiUrl + botToken + "/sendMessage";
        Map<String, String> requestBody = Map.of(
                "chat_id", chatId,
                "text", message,
                "parse_mode", "HTML",
                "disable_web_page_preview", "true"
        );

        try {
            ResponseEntity<String> response =  restTemplate.postForEntity(url, requestBody, String.class);
            log.info("Telegram notification sent to chat {}", chatId);
        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("User {} blocked the bot. Needs to be unlinked", chatId);
            // TODO
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("Telegram API BadRequest for chat {}: {}. Message: {}", chatId, e.getResponseBodyAsString(), message);
        } catch (Exception e) {
            log.warn("Failed to send telegram msg to {}, retrying... Error: {}", chatId, e.getMessage());
            throw e;
        }
    }
}
