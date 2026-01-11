package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.TrainingStatus;
import com.dev.quikkkk.enums.TrainingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

@Entity
@Table(
        name = "training_sessions",
        indexes = {
                @Index(
                        name = "idx_session_start_time",
                        columnList = "start_time"
                ),
                @Index(
                        name = "idx_session_trainer_status",
                        columnList = "trainer_id, training_status"
                ),
                @Index(
                        name = "idx_session_status_end",
                        columnList = "training_status, end_time"
                ),
                @Index(
                        name = "idx_session_type_start",
                        columnList = "training_type, start_time"
                ),
                @Index(
                        name = "idx_session_trainer_start_status",
                        columnList = "trainer_id, start_time, training_status"
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class TrainingSession extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "training_type", nullable = false)
    private TrainingType type;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_status", nullable = false)
    private TrainingStatus status;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerProfile trainer;

    @ManyToMany
    @JoinTable(
            name = "training_client",
            joinColumns = @JoinColumn(name = "training_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"),
            indexes = {
                    @Index(name = "idx_training_client_training", columnList = "training_id"),
                    @Index(name = "idx_training_client_client", columnList = "client_id")
            }
    )
    private Set<ClientProfile> clients = new HashSet<>();

    @OneToMany(mappedBy = "session")
    private Set<Attendance> attendances = new HashSet<>();
}
