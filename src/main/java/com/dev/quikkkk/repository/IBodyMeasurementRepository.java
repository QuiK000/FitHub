package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IBodyMeasurementRepository extends JpaRepository<BodyMeasurement, String> {
    Optional<BodyMeasurement> findFirstByClient_IdAndMeasurementDateBeforeOrderByMeasurementDateDesc(
            String clientId, LocalDateTime date
    );
}
