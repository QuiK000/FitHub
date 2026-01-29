package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WorkoutLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkoutLogRepository extends JpaRepository<WorkoutLog, String> {
    Page<WorkoutLog> findAllByCreatedBy(String userId, Pageable pageable);

    @Query("""
            SELECT wl FROM WorkoutLog wl
            WHERE wl.clientWorkoutPlan.id = :assignmentId
            ORDER BY wl.workoutDate DESC
            """)
    Page<WorkoutLog> findByAssignmentId(@Param("assignmentId") String assignmentId, Pageable pageable);
}
