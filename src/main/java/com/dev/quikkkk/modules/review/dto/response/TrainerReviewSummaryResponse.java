package com.dev.quikkkk.modules.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TrainerReviewSummaryResponse {
    private String trainerId;
    private Double averageRating;
    private Integer totalReviews;
    private Double professionalismAverage;
    private Double knowledgeAverage;
    private Double communicationAverage;
    private Double motivationAverage;
    private RatingDistributionDto ratingDistribution;
    private List<TrainerReviewResponse> recentReviews;
}
