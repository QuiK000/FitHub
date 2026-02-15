package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.ClientGender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "client_profiles",
        indexes = {
                @Index(
                        name = "idx_client_user",
                        columnList = "user_id",
                        unique = true
                ),
                @Index(
                        name = "idx_client_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_client_names",
                        columnList = "last_name, first_name"
                ),
                @Index(
                        name = "idx_client_phone",
                        columnList = "phone"
                )
        }
)
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfile extends BaseEntity {
    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthdate;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "daily_water_target")
    private Integer dailyWaterTarget;

    @Column(name = "client_gender")
    @Enumerated(EnumType.STRING)
    private ClientGender gender;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "client")
    private List<Membership> memberships;

    @ManyToMany(mappedBy = "clients")
    private Set<TrainingSession> trainingSessions = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<Attendance> attendances = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<ClientWorkoutPlan> workoutPlans = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<MealPlan> mealPlans = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<WaterIntake> waterIntakes = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<BodyMeasurement> bodyMeasurements = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<Goal> goals = new HashSet<>();

    public void clearPersonalData() {
        this.firstname = null;
        this.lastname = null;
        this.phone = null;
        this.birthdate = null;
    }

    public int resolveDailyWaterTarget() {
        if (dailyWaterTarget != null) return dailyWaterTarget;
        if (weight > 0) return (int) (weight * 35);
        return 2500;
    }
}
