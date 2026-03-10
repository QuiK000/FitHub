package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findAllByRecipientId(String userId, Pageable pageable);

    void deleteByReadTrueAndCreatedDateBefore(LocalDateTime thirtyDaysAgo);

    Optional<Notification> findByIdAndRecipientId(String notificationId, String userId);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.read = true,
                n.readAt = :now
        WHERE n.recipient.id = :recipientId
        AND n.read = false
        """)
    int markAllAsReadByRecipientId(String recipientId, LocalDateTime now);
}
