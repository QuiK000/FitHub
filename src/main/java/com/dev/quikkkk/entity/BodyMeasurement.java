package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.MeasurementType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "body_measurements")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BodyMeasurement extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @Column(name = "measurement_date", nullable = false)
    private LocalDateTime measurementDate;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "body_fat_percentage")
    private Double bodyFatPercentage;

    @Column(name = "muscle_mass")
    private Double muscleMass;

    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "bmw")
    private Integer bmr;

    @Column(name = "body_water_percentage")
    private Double bodyWaterPercentage;

    @Column(name = "body_mass")
    private Double boneMass;

    @Column(name = "visceral_fat_level")
    private Integer visceralFatLevel;

    @ElementCollection
    @CollectionTable(name = "body_measurement_details", joinColumns = @JoinColumn(name = "measurement_id"))
    @MapKeyColumn(name = "measurement_type")
    @Column(name = "value")
    private Map<MeasurementType, Double> measurements = new HashMap<>();

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "photo_url")
    private String photoUrl;
}
