package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMealPlanRepository extends JpaRepository<MealPlan, String> {
}
