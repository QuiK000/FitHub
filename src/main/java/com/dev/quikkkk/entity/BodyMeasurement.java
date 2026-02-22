package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.ClientGender;
import com.dev.quikkkk.enums.MeasurementType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Column(name = "bmr")
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

    @OneToMany(mappedBy = "measurement")
    private Set<ProgressPhoto> progressPhotos;

    public void calculateDerivedMetrics(Double heightCm, LocalDate birthDate, ClientGender gender) {
        if (this.bmi == null && this.weight != null && heightCm != null && heightCm > 0) {
            double heightInMeters = heightCm / 100.0;
            double calculatedBmi = this.weight / (heightInMeters * heightInMeters);
            this.bmi = Math.round(calculatedBmi * 100.0) / 100.0;
        }

        if (this.bmr == null && this.weight != null && heightCm != null && birthDate != null && gender != null) {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            double bmrCalc;

            if (gender == ClientGender.MALE) {
                bmrCalc = (10 * this.weight) + (6.25 * heightCm) - (5 * age) + 5;
            } else {
                bmrCalc = (10 * this.weight) + (6.25 * heightCm) - (5 * age) - 161;
            }
            this.bmr = (int) Math.round(bmrCalc);
        }
    }
}
