package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.ClientWorkoutStatus;
import com.dev.quikkkk.exception.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.dev.quikkkk.enums.ErrorCode.INVALID_ASSIGNMENT_STATUS;

@Entity
@Table(name = "client_workout_plans")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ClientWorkoutPlan extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClientWorkoutStatus status;

    @Column(name = "completion_percentage")
    private Double completionPercentage;

    @OneToMany(mappedBy = "clientWorkoutPlan", cascade = CascadeType.ALL)
    private Set<WorkoutLog> workoutLogs = new HashSet<>();

    public void start() {
        ensureStatus(ClientWorkoutStatus.ASSIGNED);
        status = ClientWorkoutStatus.IN_PROGRESS;
        startDate = LocalDateTime.now();
    }

    public void complete() {
        ensureStatus(ClientWorkoutStatus.IN_PROGRESS);
        status = ClientWorkoutStatus.COMPLETED;
        endDate = LocalDateTime.now();
        completionPercentage = 100.0;
    }

    public void cancel() {
        if (status == ClientWorkoutStatus.COMPLETED) throw new BusinessException(INVALID_ASSIGNMENT_STATUS);
        status = ClientWorkoutStatus.CANCELLED;
        endDate = LocalDateTime.now();
    }

    private void ensureStatus(ClientWorkoutStatus expected) {
        if (status != expected) {
            throw new BusinessException(INVALID_ASSIGNMENT_STATUS);
        }
    }
}
