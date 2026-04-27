package com.dev.quikkkk.modules.review.listener;

import com.dev.quikkkk.modules.notification.dto.request.CreateNotificationRequest;
import com.dev.quikkkk.modules.notification.enums.NotificationPriority;
import com.dev.quikkkk.modules.notification.enums.NotificationType;
import com.dev.quikkkk.modules.notification.service.INotificationService;
import com.dev.quikkkk.modules.review.event.ReviewModeratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewModerationListener {
    private final INotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewModerated(ReviewModeratedEvent event) {
        log.info("Client {} review was moderated. Sending notification.", event.getClientId());

        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .recipientId(event.getClientId())
                .type(NotificationType.NEW_REVIEW)
                .priority(NotificationPriority.NORMAL)
                .title("Review moderation update")
                .message(event.getMessage())
                .referenceType("REVIEW")
                .build();

        notificationService.sendNotification(request);
    }
}
