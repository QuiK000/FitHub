package com.dev.quikkkk.modules.nutrition.repository;

import com.dev.quikkkk.modules.nutrition.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMealFoodRepository extends JpaRepository<MealFood, String> {
}
