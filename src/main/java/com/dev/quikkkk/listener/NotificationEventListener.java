package com.dev.quikkkk.listener;

import com.dev.quikkkk.event.NotificationEvent;
import com.dev.quikkkk.service.INotificationService;
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

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Отримано подію для створення нотифікації користувачу: {}", event.getRecipientId());
        notificationService.createNotificationFromEvent(event);
    }
}
