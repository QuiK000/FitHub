package com.dev.quikkkk.modules.workout.entity;

import com.dev.quikkkk.core.entity.BaseEntity;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.workout.enums.WaitlistStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "session_waitlist",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_waitlist_session_client",
                        columnNames = {"session_id", "client_id"}
                ),
                @UniqueConstraint(
                        name = "uk_waitlist_session_position",
                        columnNames = {"session_id", "position"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_waitlist_session_status_pos",
                        columnList = "session_id, status, position"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SessionWaitlist extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile client;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WaitlistStatus status;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}
