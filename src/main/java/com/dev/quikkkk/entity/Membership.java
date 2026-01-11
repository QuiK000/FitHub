package com.dev.quikkkk.entity;

import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
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
        name = "memberships",
        indexes = {
                @Index(
                        name = "idx_membership_client_status",
                        columnList = "client_id, membership_status"
                ),
                @Index(
                        name = "idx_membership_end_date",
                        columnList = "end_date"
                ),
                @Index(
                        name = "idx_membership_status_freeze",
                        columnList = "membership_status, freeze_date"
                ),
                @Index(
                        name = "idx_membership_type",
                        columnList = "membership_type"
                ),
                @Index(
                        name = "idx_membership_client_created",
                        columnList = "client_id, created_date DESC"
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Membership extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status", nullable = false)
    private MembershipStatus status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "visits_left")
    private Integer visitsLeft;

    @Column(name = "freeze_date")
    private LocalDateTime freezeDate;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private ClientProfile client;
}
