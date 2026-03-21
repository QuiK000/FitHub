package com.dev.quikkkk.modules.review.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewVisibilityRequest {
    @NotNull(message = "VALIDATION.REVIEW.VISIBILITY.VISIBLE.REQUIRED")
    private Boolean visible;
    private String reason;

    public void validate() {
        if (Boolean.FALSE.equals(visible) && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("Reason is required when hiding a review");
        }
    }
}
