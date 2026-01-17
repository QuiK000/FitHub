package com.dev.quikkkk.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "trainer_profiles",
        indexes = {
                @Index(
                        name = "idx_trainer_user",
                        columnList = "user_id",
                        unique = true
                ),
                @Index(
                        name = "idx_trainer_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_trainer_names",
                        columnList = "last_name, first_name"
                ),
                @Index(
                        name = "idx_trainer_experience",
                        columnList = "experience_years"
                )
        }
)
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerProfile extends BaseEntity {
    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @ManyToMany
    @JoinTable(
            name = "trainer_specialization",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id"),
            indexes = {
                    @Index(name = "idx_trainer_spec_trainer", columnList = "trainer_id"),
                    @Index(name = "idx_trainer_spec_spec", columnList = "specialization_id")
            }
    )
    private Set<Specialization> specialization = new HashSet<>();

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "description")
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    private Set<TrainingSession> trainingSessions = new HashSet<>();

    @OneToMany(mappedBy = "trainer",  cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkoutPlan> workoutPlans = new HashSet<>();

    public void clearPersonalData() {
        this.firstname = null;
        this.lastname = null;
        this.description = null;
        this.specialization.clear();
        this.experienceYears = null;
    }
}
