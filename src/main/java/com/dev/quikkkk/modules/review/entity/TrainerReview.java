package com.dev.quikkkk.modules.review.entity;

import com.dev.quikkkk.core.entity.BaseEntity;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "trainer_reviews",
        indexes = {
                @Index(name = "idx_review_trainer", columnList = "trainer_id"),
                @Index(name = "idx_review_client", columnList = "client_id"),
                @Index(name = "idx_review_rating", columnList = "rating"),
                @Index(name = "idx_review_created", columnList = "created_date DESC"),
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "client_id",
                                "trainer_id"
                        }
                )
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerReview extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerProfile trainer;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "professionalism_rating")
    private Integer professionalismRating;

    @Column(name = "knowledge_rating")
    private Integer knowledgeRating;

    @Column(name = "communication_rating")
    private Integer communicationRating;

    @Column(name = "motivation_rating")
    private Integer motivationRating;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Column(name = "moderated_by_admin_id")
    private String moderatedByAdminId;

    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    @Column(name = "hidden_reason", length = 500)
    private String hiddenReason;

    @Column(name = "edited", nullable = false)
    private boolean edited;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;
}
