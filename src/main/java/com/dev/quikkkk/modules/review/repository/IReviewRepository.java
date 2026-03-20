package com.dev.quikkkk.modules.review.repository;

import com.dev.quikkkk.modules.review.entity.TrainerReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReviewRepository extends JpaRepository<TrainerReview, String> {
    boolean existsByTrainerIdAndReviewerId(String trainerId, String reviewerId);

    Page<TrainerReview> findAllByReviewerId(String reviewerId, Pageable pageable);

    Optional<TrainerReview> findByIdAndReviewerId(String reviewId, String reviewerId);
}
