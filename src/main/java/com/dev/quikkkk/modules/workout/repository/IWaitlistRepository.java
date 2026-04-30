package com.dev.quikkkk.modules.workout.repository;

import com.dev.quikkkk.modules.workout.entity.SessionWaitlist;
import com.dev.quikkkk.modules.workout.enums.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWaitlistRepository extends JpaRepository<SessionWaitlist, String> {
    boolean existsBySessionIdAndClientIdAndStatus(String sessionId, String clientId, WaitlistStatus status);

    @Query("SELECT MAX(w.position) FROM SessionWaitlist w WHERE w.session.id = :sessionId AND w.status = :status")
    Integer findMaxPositionBySessionIdAndStatus(@Param("sessionId") String sessionId, @Param("status") WaitlistStatus status);

    Optional<SessionWaitlist> findBySessionIdAndClientIdAndStatus(String sessionId, String clientId, WaitlistStatus status);

    List<SessionWaitlist> findBySessionIdAndStatusAndPositionGreaterThanOrderByPositionAsc(
            String sessionId, WaitlistStatus status, Integer position
    );
}
