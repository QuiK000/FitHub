package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.TrainingSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ITrainingSessionRepository extends JpaRepository<TrainingSession, String> {
    @Query("""
            SELECT DISTINCT s FROM TrainingSession s
            JOIN s.trainer t
            LEFT JOIN t.specialization spec
            WHERE
                        :search IS NULL
                        OR TRIM(:search) = ''
                        OR LOWER(s.type) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(t.firstname) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(t.lastname) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(spec.name) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<TrainingSession> findAllWithOptionalSearch(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT COUNT(c) > 0
            FROM TrainingSession s
            JOIN s.clients c
            WHERE s.id = :sessionId AND c.id = :clientId
            """)
    boolean existsClientInSession(String sessionId, String clientId);

    @Query("""
            SELECT COUNT(s) FROM TrainingSession s
            WHERE s.trainer.id = :trainerId
            """)
    long countAllSessionsByTrainer(@Param("trainerId") String trainerId);

    @Query("""
            SELECT COUNT(s)
            FROM TrainingSession s
            JOIN s.clients c
            WHERE c.id = :clientId
            AND s.startTime < CURRENT_TIMESTAMP
            """)
    long countPlannedSessionsByClient(@Param("clientId") String clientId);

    @Query("""
            SELECT COUNT(ts) > 0
            FROM TrainingSession ts
            WHERE ts.trainer.id = :trainerId
            AND ts.status = 'SCHEDULED'
            AND (
                (ts.startTime <= :endTime AND ts.endTime >= :startTime)
                )
            """)
    boolean hasOverlappingSession(
            @Param("trainerId") String trainerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("FROM TrainingSession ts WHERE ts.id = :id")
    Optional<TrainingSession> findByIdWithPessimisticLock(@Param("id") String id);
}
