package com.dev.quikkkk.modules.review.controller;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.service.IAdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {
    private final IAdminReviewService adminReviewService;

    @GetMapping
    public ResponseEntity<PageResponse<TrainerReviewResponse>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminReviewService.getReviews(page, size));
    }

    @PatchMapping("/{review-id}/hide")
    public ResponseEntity<MessageResponse> hideReview(@PathVariable("review-id") String reviewId) {
        return ResponseEntity.ok(adminReviewService.hideReview(reviewId));
    }

    @PatchMapping("/{review-id}/show")
    public ResponseEntity<MessageResponse> showReview(@PathVariable("review-id") String reviewId) {
        return ResponseEntity.ok(adminReviewService.showReview(reviewId));
    }
}
