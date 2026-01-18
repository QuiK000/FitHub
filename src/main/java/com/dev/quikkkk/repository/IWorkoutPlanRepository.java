package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWorkoutPlanRepository extends JpaRepository<WorkoutPlan, String> {
}
