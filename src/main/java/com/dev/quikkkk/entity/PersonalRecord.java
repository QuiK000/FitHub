package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.PersonalRecordUnit;
import com.dev.quikkkk.enums.RecordType;
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
        name = "personal_records",
        indexes = {
                @Index(name = "idx_pr_client_exercise", columnList = "client_id, exercise_id"),
                @Index(name = "idx_pr_date", columnList = "record_date DESC")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalRecord extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    private RecordType recordType;

    @Column(name = "value", nullable = false)
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit")
    private PersonalRecordUnit unit;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    @Column(name = "previous_record")
    private Double previousRecord;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "is_current_best")
    private boolean isCurrentBest;
}
