package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WorkoutPlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercise, String> {
}
