package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WorkoutLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IWorkoutLogRepository extends JpaRepository<WorkoutLog, String> {
    Page<WorkoutLog> findAllByCreatedBy(String userId, Pageable pageable);

    @Query("""
            SELECT wl FROM WorkoutLog wl
            WHERE wl.clientWorkoutPlan.id = :assignmentId
            ORDER BY wl.workoutDate DESC
            """)
    Page<WorkoutLog> findByAssignmentId(@Param("assignmentId") String assignmentId, Pageable pageable);

    @Query("""
            SELECT wl FROM WorkoutLog wl
            WHERE wl.exercise.id = :exerciseId
            ORDER BY wl.workoutDate DESC
            """)
    Page<WorkoutLog> findByExerciseId(@Param("exerciseId") String exerciseId, Pageable pageable);

    @Query("""
            SELECT wl FROM WorkoutLog wl
            WHERE wl.workoutDate >= :from AND wl.workoutDate < :to
            ORDER BY wl.workoutDate DESC
            """)
    Page<WorkoutLog> findByDateRangeForAdmin(
            @Param("from") LocalDateTime fromDateTime,
            @Param("to") LocalDateTime toDateTime,
            Pageable pageable
    );

    @Query("""
            SELECT wl FROM WorkoutLog wl
            JOIN wl.clientWorkoutPlan cwp
            JOIN cwp.workoutPlan wp
            WHERE wp.trainer.id = :trainerId
            AND wl.workoutDate >= :from
            AND wl.workoutDate < :to
            ORDER BY wl.workoutDate DESC
            """)
    Page<WorkoutLog> findByDateRangeForTrainer(
            @Param("from") LocalDateTime fromDateTime,
            @Param("to") LocalDateTime toDateTime,
            @Param("trainerId") String id,
            Pageable pageable
    );

    @Query("""
            SELECT wl FROM WorkoutLog wl
            WHERE wl.workoutDate >= :from AND wl.workoutDate < :to
            AND wl.createdBy = :userId
            ORDER BY wl.workoutDate DESC
            """)
    Page<WorkoutLog> findByDateRangeAndUserId(
            @Param("from") LocalDateTime fromDateTime,
            @Param("to") LocalDateTime toDateTime,
            @Param("userId") String userId,
            Pageable pageable
    );
}
