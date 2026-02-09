package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IWaterIntakeRepository extends JpaRepository<WaterIntake, String> {
    @Query("""
            SELECT COALESCE(SUM(w.amountMl), 0)
            FROM WaterIntake w
            WHERE w.client.id = :clientId
            AND w.intakeDate = :date
            """)
    Integer sumAmountByClientIdAndIntakeDate(@Param("clientId") String clientId, @Param("date") LocalDate intakeDate);

    List<WaterIntake> findAllByClientIdAndIntakeDateBetweenOrderByIntakeTimeAsc(
            String clientId,
            LocalDate startDate,
            LocalDate endDate
    );
}
