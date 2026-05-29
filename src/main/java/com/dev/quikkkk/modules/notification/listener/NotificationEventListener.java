package com.dev.quikkkk.modules.notification.listener;

import com.dev.quikkkk.modules.notification.event.NotificationEvent;
import com.dev.quikkkk.modules.notification.service.INotificationService;
import com.dev.quikkkk.modules.notification.service.ITelegramNotificationService;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final INotificationService notificationService;
    private final ITelegramNotificationService  telegramNotificationService;
    private final IUserRepository userRepository;

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Отримано подію для створення нотифікації користувачу: {}", event.getRecipientId());
        notificationService.createNotificationFromEvent(event);

        userRepository.findById(event.getRecipientId()).ifPresent(user -> {
            if (user.getTelegramChatId() != null && !user.getTelegramChatId().isEmpty()) {
                String tgMessage = formatForTelegram(event);
                telegramNotificationService.sendNotification(user.getTelegramChatId(), tgMessage);
            }
        });
    }

    private String formatForTelegram(NotificationEvent event) {
        String icon = switch (event.getPriority()) {
            case HIGH -> "🚨";
            case MEDIUM -> "⚠️";
            default -> "ℹ️";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(icon).append(" ").append(escapeHtml(event.getTitle())).append("</b>\n\n");
        sb.append(escapeHtml(event.getMessage()));

        if (event.getActionUrl() != null && !event.getActionUrl().isEmpty()) {
            sb.append("\n\n🔗 <a href=\"").append(event.getActionUrl()).append("\">Перейти к деталям</a>");
        }

        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("&", "&amp;");
    }
}
