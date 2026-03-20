package com.dev.quikkkk.modules.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpdateTrainerReviewRequest {
    @Min(value = 1, message = "VALIDATION.REVIEW.RATING.MIN")
    @Max(value = 5, message = "VALIDATION.REVIEW.RATING.MAX")
    private Integer rating;

    @Size(max = 1000, message = "VALIDATION.REVIEW.COMMENT.SIZE")
    private String comment;

    @Min(value = 1, message = "VALIDATION.REVIEW.PROFESSIONALISM.MIN")
    @Max(value = 5, message = "VALIDATION.REVIEW.PROFESSIONALISM.MAX")
    private Integer professionalismRating;

    @Min(value = 1, message = "VALIDATION.REVIEW.KNOWLEDGE.MIN")
    @Max(value = 5, message = "VALIDATION.REVIEW.KNOWLEDGE.MAX")
    private Integer knowledgeRating;

    @Min(value = 1, message = "VALIDATION.REVIEW.COMMUNICATION.MIN")
    @Max(value = 5, message = "VALIDATION.REVIEW.COMMUNICATION.MAX")
    private Integer communicationRating;

    @Min(value = 1, message = "VALIDATION.REVIEW.MOTIVATION.MIN")
    @Max(value = 5, message = "VALIDATION.REVIEW.MOTIVATION.MAX")
    private Integer motivationRating;
}
