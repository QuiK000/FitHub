package com.dev.quikkkk.modules.review.service.impl;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.utils.PaginationUtils;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.mapper.ReviewMapper;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.review.service.IAdminReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_ALREADY_HIDDEN;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_ALREADY_VISIBLE;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminReviewServiceImpl implements IAdminReviewService {
    private final IReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final MessageMapper messageMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TrainerReviewResponse> getReviews(int page, int size) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<TrainerReview> trainerReviewPage = reviewRepository.findAll(pageable);

        return PaginationUtils.toPageResponse(trainerReviewPage, reviewMapper::toResponse);
    }

    @Override
    public MessageResponse hideReview(String reviewId) {
        TrainerReview review = findReviewById(reviewId);
        if (!review.isVisible()) throw new BusinessException(REVIEW_ALREADY_HIDDEN);

        review.setVisible(false);
        return messageMapper.message("[" + review.getId() + "]" + " Review has been hided");
    }

    @Override
    public MessageResponse showReview(String reviewId) {
        TrainerReview review = findReviewById(reviewId);
        if (review.isVisible()) throw new BusinessException(REVIEW_ALREADY_VISIBLE);

        review.setVisible(true);
        return messageMapper.message("[" + review.getId() + "]" + " Review has been visible");
    }

    private TrainerReview findReviewById(String reviewId) {
        return reviewRepository
                .findById(reviewId)
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));
    }
}
