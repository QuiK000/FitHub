package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IMealPlanRepository extends JpaRepository<MealPlan, String> {
    @Query("FROM MealPlan mp WHERE mp.client.id = :clientId AND mp.planDate = :planDate")
    Optional<MealPlan> findByClientIdAndPlanDate(
            @Param("clientId") String clientId,
            @Param("planDate") LocalDate planDate
    );
}
