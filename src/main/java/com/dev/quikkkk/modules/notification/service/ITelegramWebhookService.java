package com.dev.quikkkk.modules.notification.service;

import com.dev.quikkkk.modules.notification.dto.telegram.TelegramUpdate;

public interface ITelegramWebhookService {
    void processUpdate(TelegramUpdate update);
}
