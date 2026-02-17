package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IGoalRepository extends JpaRepository<Goal, String> {
    Page<Goal> getGoalsByClientId(Pageable pageable, String clientId);

    @Query("""
            SELECT g FROM Goal g
            WHERE g.client.id = :clientId
            AND g.status = 'ACTIVE'
            """)
    Page<Goal> getActiveGoals(@Param("clientId") String clientId, Pageable pageable);

    @Query("""
            SELECT g FROM Goal g
            WHERE g.client.id = :clientId
            AND g.status = 'COMPLETED'
            """)
    Page<Goal> getCompletedGoals(@Param("clientId") String clientId, Pageable pageable);
}
