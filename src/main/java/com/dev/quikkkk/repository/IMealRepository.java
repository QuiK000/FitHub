package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMealRepository extends JpaRepository<Meal, String> {
}
