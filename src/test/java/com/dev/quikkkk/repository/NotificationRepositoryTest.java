package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.notification.entity.Notification;
import com.dev.quikkkk.modules.notification.enums.NotificationPriority;
import com.dev.quikkkk.modules.notification.enums.NotificationType;
import com.dev.quikkkk.modules.notification.repository.INotificationRepository;
import com.dev.quikkkk.modules.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
@DisplayName("NotificationRepository Tests")
class NotificationRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private INotificationRepository notificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find notifications by recipient id")
    void findAllByRecipientId_WithNotifications_ReturnsPage() {
        User recipient = persistUser("recipient@test.com");
        persistNotification(recipient, "Test Title", false);

        Page<Notification> result = notificationRepository.findAllByRecipientId(
                recipient.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRecipient().getId()).isEqualTo(recipient.getId());
    }

    @Test
    @DisplayName("Should count unread notifications")
    void countAllByRecipientIdAndReadIsFalse_WithUnreadNotifications_ReturnsCount() {
        User recipient = persistUser("unread@test.com");
        persistNotification(recipient, "Unread 1", false);
        persistNotification(recipient, "Unread 2", false);
        persistNotification(recipient, "Read 1", true);

        long count = notificationRepository.countAllByRecipientIdAndReadIsFalse(recipient.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should mark all notifications as read")
    void markAllAsReadByRecipientId_WithUnreadNotifications_MarksAllAsRead() {
        User recipient = persistUser("markread@test.com");
        persistNotification(recipient, "Unread 1", false);
        persistNotification(recipient, "Unread 2", false);

        int updated = notificationRepository.markAllAsReadByRecipientId(
                recipient.getId(), LocalDateTime.now());

        assertThat(updated).isEqualTo(2);
    }

    @Test
    @DisplayName("Should delete old read notifications")
    void deleteByReadTrueAndCreatedDateBefore_WithOldReadNotifications_DeletesThem() {
        User recipient = persistUser("cleanup@test.com");
        Notification oldRead = persistNotification(recipient, "Old", true);
        oldRead.setCreatedDate(LocalDateTime.now().minusDays(60));
        entityManager.persist(oldRead);
        entityManager.flush();

        notificationRepository.deleteByReadTrueAndCreatedDateBefore(LocalDateTime.now().minusDays(30));

        assertThat(notificationRepository.findById(oldRead.getId())).isEmpty();
    }

    private User persistUser(String email) {
        User user = User.builder()
                .email(email)
                .password("encoded")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Notification persistNotification(User recipient, String title, boolean read) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message("Message for " + title)
                .type(NotificationType.GENERAL_ANNOUNCEMENT)
                .priority(NotificationPriority.LOW)
                .read(read)
                .sent(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(notification);
        entityManager.flush();
        return notification;
    }
}
