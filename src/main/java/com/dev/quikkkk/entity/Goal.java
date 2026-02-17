package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.enums.GoalStatus;
import com.dev.quikkkk.enums.GoalType;
import com.dev.quikkkk.enums.MeasurementUnit;
import com.dev.quikkkk.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "goals",
        indexes = {
                @Index(name = "idx_goal_client", columnList = "client_id"),
                @Index(name = "idx_goal_status", columnList = "status")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Goal extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    @Column(name = "start_value")
    private Double startValue;

    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "current_value")
    private Double currentValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit")
    private MeasurementUnit unit;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status;

    @Column(name = "progress_percentage")
    private Double progressPercentage;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Version
    private Long version;

    public void trackProgress(Double newValue, String newNotes) {
        if (!status.equals(GoalStatus.ACTIVE)) throw new BusinessException(ErrorCode.GOAL_NOT_ACTIVE);
        currentValue = newValue;

        if (newNotes != null && !newNotes.isBlank()) notes = newNotes;
        recalculateProgressPercentage();
    }

    public void complete() {
        if (status == GoalStatus.COMPLETED) throw new BusinessException(ErrorCode.GOAL_ALREADY_COMPLETED);

        status = GoalStatus.COMPLETED;
        completionDate = LocalDateTime.now();
        progressPercentage = 100.0;
    }

    private void recalculateProgressPercentage() {
        if (startValue == null || targetValue == null || currentValue == null) {
            this.progressPercentage = 0.0;
            return;
        }

        if (startValue.equals(targetValue)) {
            this.progressPercentage = 100.0;
            return;
        }

        double totalDiff = targetValue - startValue;
        double currentDiff = currentValue - startValue;
        double percentage = (currentDiff / totalDiff) * 100;

        progressPercentage = Math.min(100.0, Math.max(0.0, percentage));
    }
}
