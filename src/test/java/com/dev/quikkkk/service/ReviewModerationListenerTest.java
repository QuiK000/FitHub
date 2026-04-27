package com.dev.quikkkk.service;

import com.dev.quikkkk.modules.notification.dto.request.CreateNotificationRequest;
import com.dev.quikkkk.modules.notification.enums.NotificationType;
import com.dev.quikkkk.modules.notification.service.INotificationService;
import com.dev.quikkkk.modules.review.event.ReviewModeratedEvent;
import com.dev.quikkkk.modules.review.listener.ReviewModerationListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewModerationListener Tests")
public class ReviewModerationListenerTest {
    @Mock
    private INotificationService notificationService;

    @InjectMocks
    private ReviewModerationListener reviewModerationListener;

    @Test
    @DisplayName("Should dispatch in-app notification on review moderation")
    void handleReviewModerated_ShouldSendNotification() {
        ReviewModeratedEvent event = new ReviewModeratedEvent(
                this,
                "client-id",
                "Your review was hidden by moderator"
        );

        reviewModerationListener.handleReviewModerated(event);

        ArgumentCaptor<CreateNotificationRequest> requestCaptor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService).sendNotification(requestCaptor.capture());
        CreateNotificationRequest captured = requestCaptor.getValue();

        assertThat(captured.getRecipientId()).isEqualTo("client-id");
        assertThat(captured.getType()).isEqualTo(NotificationType.NEW_REVIEW);
        assertThat(captured.getMessage()).isEqualTo("Your review was hidden by moderator");
    }
}
