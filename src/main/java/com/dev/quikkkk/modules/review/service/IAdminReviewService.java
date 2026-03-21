package com.dev.quikkkk.modules.review.service;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.review.dto.request.AdminReviewFilterRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateReviewVisibilityRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;

public interface IAdminReviewService {
    PageResponse<TrainerReviewResponse> getReviews(AdminReviewFilterRequest request, int page, int size);

    TrainerReviewResponse updateReviewVisibility(String reviewId, UpdateReviewVisibilityRequest request);
}
