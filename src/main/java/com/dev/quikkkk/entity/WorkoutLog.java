package com.dev.quikkkk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "workout_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class WorkoutLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_workout_plan_id", nullable = false)
    private ClientWorkoutPlan clientWorkoutPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "workout_date", nullable = false)
    private LocalDateTime workoutDate;

    @Column(name = "sets_completed")
    private Integer setsCompleted;

    @Column(name = "reps_completed")
    private Integer repsCompleted;

    @Column(name = "weight_used")
    private Double weightUsed;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "difficulty_rating")
    private Integer difficultyRating;
}
