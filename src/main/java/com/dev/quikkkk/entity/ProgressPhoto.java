package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.PhotoAngle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "progress_photos",
        indexes = {
                @Index(name = "idx_photo_client_date", columnList = "client_id, photo_date DESC")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressPhoto extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @Column(name = "photo_date", nullable = false)
    private LocalDateTime photoDate;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "angle")
    private PhotoAngle angle;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id")
    private BodyMeasurement measurement;
}
