package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findNotificationsByRecipientId(String userId, Pageable pageable);

    void deleteByReadTrueAndCreatedDateBefore(LocalDateTime thirtyDaysAgo);
}
