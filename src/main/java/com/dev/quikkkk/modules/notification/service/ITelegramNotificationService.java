package com.dev.quikkkk.modules.notification.service;

public interface ITelegramNotificationService {
    void sendNotification(String chatId, String message);
}
