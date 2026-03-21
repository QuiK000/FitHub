package com.dev.quikkkk.modules.review.service;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;

public interface IAdminReviewService {
    PageResponse<TrainerReviewResponse> getReviews(int page, int size);

    MessageResponse hideReview(String reviewId);

    MessageResponse showReview(String reviewId);
}
