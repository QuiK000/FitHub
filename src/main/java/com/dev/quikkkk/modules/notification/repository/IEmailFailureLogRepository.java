package com.dev.quikkkk.modules.notification.repository;

import com.dev.quikkkk.modules.notification.entity.EmailFailureLog;
import com.dev.quikkkk.modules.notification.enums.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IEmailFailureLogRepository extends JpaRepository<EmailFailureLog, String> {
    @Query("""
            SELECT e FROM EmailFailureLog e
            WHERE e.retryScheduled = true
            AND e.nextRetryAt <= :now
            AND e.attemptCount < :maxAttempts
            """)
    List<EmailFailureLog> findEmailForRetry(LocalDateTime now, int maxAttempts);

    List<EmailFailureLog> findByRecipientEmailAndEmailType(String email, EmailType emailType);
}
