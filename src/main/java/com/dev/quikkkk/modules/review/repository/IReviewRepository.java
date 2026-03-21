package com.dev.quikkkk.modules.review.repository;

import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.repository.projection.IReviewAggregationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IReviewRepository extends
        JpaRepository<TrainerReview, String>,
        JpaSpecificationExecutor<TrainerReview> {
    boolean existsByTrainerIdAndReviewerId(String trainerId, String reviewerId);

    Page<TrainerReview> findAllByReviewerId(String reviewerId, Pageable pageable);

    Optional<TrainerReview> findByIdAndReviewerId(String reviewId, String reviewerId);

    @Query(value = """
                SELECT r FROM TrainerReview r
                JOIN FETCH r.reviewer c
                WHERE r.trainer.id = :trainerId
                  AND r.visible = true
            """, countQuery = "SELECT COUNT(r) FROM TrainerReview r WHERE r.trainer.id = :trainerId AND r.visible = true")
    Page<TrainerReview> findAllPublicByTrainerId(@Param("trainerId") String trainerId, Pageable pageable);

    @Query("""
                SELECT
                    COUNT(r) as totalReviews,
                    COALESCE(AVG(r.rating), 0.0) as averageRating,
                    COALESCE(AVG(r.professionalismRating), 0.0) as professionalismAverage,
                    COALESCE(AVG(r.knowledgeRating), 0.0) as knowledgeAverage,
                    COALESCE(AVG(r.communicationRating), 0.0) as communicationAverage,
                    COALESCE(AVG(r.motivationRating), 0.0) as motivationAverage,
                    SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END) as fiveStars,
                    SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END) as fourStars,
                    SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END) as threeStars,
                    SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END) as twoStars,
                    SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END) as oneStar
                FROM TrainerReview r
                WHERE r.trainer.id = :trainerId
                  AND r.visible = true
            """)
    IReviewAggregationProjection getReviewAggregationByTrainerId(@Param("trainerId") String trainerId);

    @Query("""
                SELECT r FROM TrainerReview r
                JOIN FETCH r.reviewer c
                WHERE r.trainer.id = :trainerId
                  AND r.visible = true
                ORDER BY r.createdDate DESC
            """)
    List<TrainerReview> findRecentReviewsWithClientByTrainerId(@Param("trainerId") String trainerId, Pageable pageable);
}
