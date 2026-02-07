package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWaterIntakeRepository extends JpaRepository<WaterIntake, String> {
}
