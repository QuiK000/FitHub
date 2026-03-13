package com.dev.quikkkk.modules.nutrition.repository;

import com.dev.quikkkk.modules.nutrition.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMealRepository extends JpaRepository<Meal, String> {
}
