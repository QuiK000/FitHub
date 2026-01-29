package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWorkoutLogRepository extends JpaRepository<WorkoutLog, String> {
    List<WorkoutLog> findAllByCreatedBy(String userId);
}
