package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.ClientWorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientWorkoutPlanRepository extends JpaRepository<ClientWorkoutPlan, String> {
}
