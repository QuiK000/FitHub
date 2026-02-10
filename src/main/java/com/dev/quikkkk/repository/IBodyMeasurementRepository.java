package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBodyMeasurementRepository extends JpaRepository<BodyMeasurement, String> {
}
