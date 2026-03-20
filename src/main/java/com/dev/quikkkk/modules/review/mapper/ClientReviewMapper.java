package com.dev.quikkkk.modules.review.mapper;

import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.user.dto.response.ClientShortResponse;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import org.springframework.stereotype.Service;

@Service
public class ClientReviewMapper {
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
}
