package com.dev.quikkkk.modules.workout.repository;

import com.dev.quikkkk.modules.workout.entity.WorkoutPlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercise, String> {
}
