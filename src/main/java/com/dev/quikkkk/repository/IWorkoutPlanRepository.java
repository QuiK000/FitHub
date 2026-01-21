package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkoutPlanRepository extends JpaRepository<WorkoutPlan, String> {
    @Query("""
            SELECT wp FROM WorkoutPlan wp
            WHERE wp.active = true
            AND wp.difficultyLevel = :difficulty
            """)
    Page<WorkoutPlan> findWorkoutPlanByDifficulty(Pageable pageable, @Param("difficulty") DifficultyLevel difficulty);

    Page<WorkoutPlan> findWorkoutPlansByTrainerId(Pageable pageable, String trainerId);
}
