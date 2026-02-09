package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IWaterIntakeRepository extends JpaRepository<WaterIntake, String> {
    List<WaterIntake> findALlByClientIdAndIntakeDate(String clientId, LocalDate intakeDate);
}
