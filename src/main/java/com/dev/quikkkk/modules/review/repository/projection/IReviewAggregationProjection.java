package com.dev.quikkkk.modules.review.repository.projection;

public interface IReviewAggregationProjection {
    Long getTotalReviews();
    Double getAverageRating();
    Double getProfessionalismAverage();
    Double getKnowledgeAverage();
    Double getCommunicationAverage();
    Double getMotivationAverage();

    Integer getFiveStars();
    Integer getFourStars();
    Integer getThreeStars();
    Integer getTwoStars();
    Integer getOneStar();
}
