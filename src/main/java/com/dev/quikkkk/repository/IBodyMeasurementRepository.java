package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.BodyMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IBodyMeasurementRepository extends JpaRepository<BodyMeasurement, String> {
    @Query("""
            SELECT b FROM BodyMeasurement b
            WHERE b.client.id = :clientId AND b.measurementDate < :date
            ORDER BY b.measurementDate DESC LIMIT 1
            """)
    Optional<BodyMeasurement> findPreviousMeasurement(
            @Param("clientId") String clientId,
            @Param("date") LocalDateTime date
    );

    Page<BodyMeasurement> findBodyMeasurementsByClientId(String clientId, Pageable pageable);

    Optional<BodyMeasurement> findFirstByClientIdOrderByMeasurementDateDesc(String clientId);

    List<BodyMeasurement> findAllBodyMeasurementsByClientId(String clientId);
}
