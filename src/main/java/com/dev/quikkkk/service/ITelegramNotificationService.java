package com.dev.quikkkk.service;

public interface ITelegramNotificationService {
    void sendNotification(String chatId, String message);
}
