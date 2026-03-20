package com.dev.quikkkk.modules.review.service;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewSummaryResponse;

public interface ITrainerReviewService {
    PageResponse<TrainerReviewResponse> getReviewsByTrainerId(String trainerId, int page, int size);

    PageResponse<TrainerReviewResponse> getMyReviews(int page, int size);

    TrainerReviewSummaryResponse getMyReviewSummary();
}
