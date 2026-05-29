package com.dev.quikkkk.modules.notification.service.impl;

import com.dev.quikkkk.modules.notification.dto.telegram.TelegramUpdate;
import com.dev.quikkkk.modules.notification.service.ITelegramNotificationService;
import com.dev.quikkkk.modules.notification.service.ITelegramWebhookService;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhookServiceImpl implements ITelegramWebhookService {
    private final IUserRepository userRepository;
    private final ITelegramNotificationService telegramNotificationService;

    @Override
    @Transactional
    public void processUpdate(TelegramUpdate update) {
        if (update.getMessage() == null || update.getMessage().getText() == null) return;

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChat().getId();

        if (text.startsWith("/start ")) {
            String userId = text.replace("/start", "").trim();
            linkUserAccount(userId, String.valueOf(chatId));
        }
    }

    private void linkUserAccount(String userId, String telegramChatId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setTelegramChatId(telegramChatId);
                userRepository.save(user);

                String successMsg = "✅ <b>Успешно!</b>\nВаш аккаунт FitHub привязан. Теперь вы будете получать уведомления сюда.";
                telegramNotificationService.sendNotification(telegramChatId, successMsg);

                log.info("Linked Telegram chat {} to user {}", telegramChatId, userId);
            } else {
                telegramNotificationService.sendNotification(telegramChatId, "❌ Пользователь не найден. Попробуйте сгенерировать ссылку заново.");
            }
        } catch (Exception e) {
            log.error("Error linking telegram account", e);
        }
    }
}
