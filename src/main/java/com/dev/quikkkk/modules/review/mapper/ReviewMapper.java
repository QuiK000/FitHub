package com.dev.quikkkk.modules.review.mapper;

import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.RatingDistributionDto;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewSummaryResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.repository.projection.IReviewAggregationProjection;
import com.dev.quikkkk.modules.user.dto.response.ClientShortResponse;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewMapper {
    public TrainerReview toEntity(
            TrainerProfile trainer,
            ClientProfile client,
            CreateTrainerReviewRequest request
    ) {
        return TrainerReview.builder()
                .reviewer(client)
                .trainer(trainer)
                .rating(request.getRating())
                .comment(request.getComment())
                .professionalismRating(request.getProfessionalismRating())
                .knowledgeRating(request.getKnowledgeRating())
                .communicationRating(request.getCommunicationRating())
                .motivationRating(request.getMotivationRating())
                .createdBy(client.getId())
                .build();
    }

    public TrainerReviewResponse toResponse(TrainerReview review) {
        return TrainerReviewResponse.builder()
                .id(review.getId())
                .reviewer(
                        ClientShortResponse.builder()
                                .clientId(review.getReviewer().getId())
                                .clientFirstname(review.getReviewer().getFirstname())
                                .clientLastname(review.getReviewer().getLastname())
                                .build()
                )
                .rating(review.getRating())
                .comment(review.getComment())
                .professionalismRating(review.getProfessionalismRating())
                .knowledgeRating(review.getKnowledgeRating())
                .communicationRating(review.getCommunicationRating())
                .motivationRating(review.getMotivationRating())
                .createdAt(review.getCreatedDate())
                .edited(review.isEdited())
                .editedAt(review.getEditedAt())
                .visible(review.isVisible())
                .hiddenReason(review.getHiddenReason())
                .moderatedByAdminId(review.getModeratedByAdminId())
                .moderatedAt(review.getModeratedAt())
                .build();
    }

    public void update(UpdateTrainerReviewRequest request, TrainerReview review) {
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
        if (request.getProfessionalismRating() != null)
            review.setProfessionalismRating(request.getProfessionalismRating());
        if (request.getKnowledgeRating() != null) review.setKnowledgeRating(request.getKnowledgeRating());
        if (request.getCommunicationRating() != null) review.setCommunicationRating(request.getCommunicationRating());
        if (request.getMotivationRating() != null) review.setMotivationRating(request.getMotivationRating());
    }

    public TrainerReviewSummaryResponse toSummaryResponse(
            String trainerId,
            IReviewAggregationProjection agg,
            List<TrainerReview> recentReviews
    ) {
        if (agg == null || agg.getTotalReviews() == null || agg.getTotalReviews() == 0)
            return buildEmptySummary(trainerId);

        return TrainerReviewSummaryResponse.builder()
                .trainerId(trainerId)
                .totalReviews(agg.getTotalReviews().intValue())
                .averageRating(roundRating(agg.getAverageRating()))
                .professionalismAverage(roundRating(agg.getProfessionalismAverage()))
                .knowledgeAverage(roundRating(agg.getKnowledgeAverage()))
                .communicationAverage(roundRating(agg.getCommunicationAverage()))
                .motivationAverage(roundRating(agg.getMotivationAverage()))
                .ratingDistribution(toDistributionDto(agg))
                .recentReviews(recentReviews.stream().map(this::toResponse).toList())
                .build();
    }

    private RatingDistributionDto toDistributionDto(IReviewAggregationProjection agg) {
        return RatingDistributionDto.builder()
                .fiveStars(agg.getFiveStars() != null ? agg.getFiveStars() : 0)
                .fourStars(agg.getFourStars() != null ? agg.getFourStars() : 0)
                .threeStars(agg.getThreeStars() != null ? agg.getThreeStars() : 0)
                .twoStars(agg.getTwoStars() != null ? agg.getTwoStars() : 0)
                .oneStar(agg.getOneStar() != null ? agg.getOneStar() : 0)
                .build();
    }

    private TrainerReviewSummaryResponse buildEmptySummary(String trainerId) {
        return TrainerReviewSummaryResponse.builder()
                .trainerId(trainerId)
                .totalReviews(0)
                .averageRating(0.0)
                .professionalismAverage(0.0)
                .knowledgeAverage(0.0)
                .communicationAverage(0.0)
                .motivationAverage(0.0)
                .ratingDistribution(new RatingDistributionDto(
                        0,
                        0,
                        0,
                        0,
                        0)
                )
                .recentReviews(List.of())
                .build();
    }

    private Double roundRating(Double value) {
        return value == null ? 0.0 : Math.round(value * 10.0) / 10.0;
    }
}
