package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.ClientWorkoutPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientWorkoutPlanRepository extends JpaRepository<ClientWorkoutPlan, String> {
    @Query("FROM ClientWorkoutPlan cwp WHERE cwp.status = 'ASSIGNED'")
    Page<ClientWorkoutPlan> getAllAssignedPlans(Pageable pageable);
}
