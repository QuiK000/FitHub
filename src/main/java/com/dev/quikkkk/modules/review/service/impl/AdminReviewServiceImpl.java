package com.dev.quikkkk.modules.review.service.impl;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.utils.PaginationUtils;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.review.dto.request.AdminReviewFilterRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateReviewVisibilityRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.event.ReviewModeratedEvent;
import com.dev.quikkkk.modules.review.mapper.ReviewMapper;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.review.service.IAdminReviewService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminReviewServiceImpl implements IAdminReviewService {
    private final IReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TrainerReviewResponse> getReviews(AdminReviewFilterRequest filter, int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Specification<TrainerReview> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTrainerId() != null && !filter.getTrainerId().isBlank())
                predicates.add(cb.equal(root.get("trainer").get("id"), filter.getTrainerId()));

            if (filter.getReviewerId() != null && !filter.getReviewerId().isBlank())
                predicates.add(cb.equal(root.get("reviewer").get("id"), filter.getReviewerId()));
            if (filter.getIsVisible() != null)
                predicates.add(cb.equal(root.get("visible"), filter.getIsVisible()));
            if (filter.getRating() != null)
                predicates.add(cb.equal(root.get("rating"), filter.getRating()));

            if (Long.class != query.getResultType()) root.fetch("reviewer");
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<TrainerReview> trainerReviewPage = reviewRepository.findAll(spec, pageable);
        return PaginationUtils.toPageResponse(trainerReviewPage, reviewMapper::toResponse);
    }

    @Override
    public TrainerReviewResponse updateReviewVisibility(String reviewId, UpdateReviewVisibilityRequest request) {
        request.validate();

        TrainerReview review = findReviewById(reviewId);
        String adminId = SecurityUtils.getCurrentUserId();

        review.setVisible(request.getVisible());
        review.setHiddenReason(request.getVisible() ? null : request.getReason());
        review.setModeratedByAdminId(adminId);
        review.setModeratedAt(LocalDateTime.now());

        log.info("Admin {} changed review {} visibility to {}", adminId, reviewId, request.getVisible());
        if (!request.getVisible()) {
            String msg = "Your review was hidden by the administrator.";
            if (request.getReason() != null) {
                msg += " Reason: " + request.getReason();
            }

            publisher.publishEvent(new ReviewModeratedEvent(this, review.getReviewer().getId(), msg));
        }

        return reviewMapper.toResponse(review);
    }

    private TrainerReview findReviewById(String reviewId) {
        return reviewRepository
                .findById(reviewId)
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));
    }
}
