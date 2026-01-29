package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WorkoutLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkoutLogRepository extends JpaRepository<WorkoutLog, String> {
    Page<WorkoutLog> findAllByCreatedBy(String userId, Pageable pageable);
}
