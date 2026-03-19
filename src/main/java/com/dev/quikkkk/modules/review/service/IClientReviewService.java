package com.dev.quikkkk.modules.review.service;

import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;

public interface IClientReviewService {
    TrainerReviewResponse createReview(CreateTrainerReviewRequest request, String trainerId);
}
