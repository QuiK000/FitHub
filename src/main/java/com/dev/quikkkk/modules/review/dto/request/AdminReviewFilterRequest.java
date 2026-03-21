package com.dev.quikkkk.modules.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminReviewFilterRequest {
    private String trainerId;
    private String reviewerId;
    private Boolean isVisible;
    private Integer rating;
}
