package com.dev.quikkkk.modules.progress.repository;

import com.dev.quikkkk.modules.progress.entity.Goal;
import com.dev.quikkkk.modules.progress.enums.GoalStatus;
import com.dev.quikkkk.modules.progress.enums.GoalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGoalRepository extends JpaRepository<Goal, String> {
    Page<Goal> findAllByClientId(String clientId, Pageable pageable);

    Page<Goal> findAllByClientIdAndStatus(String clientId, GoalStatus goalStatus, Pageable pageable);

    boolean existsByClientIdAndGoalTypeAndStatus(String id, GoalType goalType, GoalStatus goalStatus);
}
