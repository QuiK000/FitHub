package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.ClientWorkoutPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IClientWorkoutPlanRepository extends JpaRepository<ClientWorkoutPlan, String> {
    @Query("FROM ClientWorkoutPlan cwp WHERE cwp.status = 'ASSIGNED'")
    Page<ClientWorkoutPlan> getAllAssignedPlans(Pageable pageable);

    List<ClientWorkoutPlan> findByClientId(String id);

    @Query("FROM ClientWorkoutPlan cwp WHERE cwp.client.id = :id AND cwp.status = 'ASSIGNED'")
    List<ClientWorkoutPlan> findByClientIdAndActiveTrue(String id);

    @Query("FROM ClientWorkoutPlan cwp WHERE cwp.client.id = :clientId AND cwp.id = :assignmentId")
    Optional<ClientWorkoutPlan> findAssignmentByClientIdAndAssignmentId(String clientId, String assignmentId);
}
