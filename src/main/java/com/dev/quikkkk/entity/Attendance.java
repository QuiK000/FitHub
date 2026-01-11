package com.dev.quikkkk.entity;

import jakarta.persistence.Entity;
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
        name = "attendances",
        indexes = {
                @Index(
                        name = "idx_attendance_client",
                        columnList = "client_id"
                ),
                @Index(
                        name = "idx_attendance_session",
                        columnList = "session_id"
                ),
                @Index(
                        name = "idx_attendance_checkin_time",
                        columnList = "check_in_time DESC"
                ),
                @Index(
                        name = "idx_attendance_client_session",
                        columnList = "client_id, session_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Attendance extends BaseEntity {
    private LocalDateTime checkInTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private ClientProfile client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id")
    private TrainingSession session;
}
