package com.dev.quikkkk.modules.review.service;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;

public interface IClientReviewService {
    TrainerReviewResponse createReview(CreateTrainerReviewRequest request, String trainerId);

    PageResponse<TrainerReviewResponse> getReviews(int page, int size);

    MessageResponse updateReview(String reviewId, UpdateTrainerReviewRequest request);
}
