package com.dev.quikkkk.modules.review.controller;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.service.IClientReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class ClientReviewController {
    private final IClientReviewService clientReviewService;

    @PostMapping("/trainers/{trainer-id}")
    public ResponseEntity<TrainerReviewResponse> createReview(
            @Valid @RequestBody CreateTrainerReviewRequest request,
            @PathVariable("trainer-id") String trainerId
    ) {
        return ResponseEntity.ok(clientReviewService.createReview(request, trainerId));
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<PageResponse<TrainerReviewResponse>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(clientReviewService.getReviews(page, size));
    }

    @PutMapping("/{review-id}")
    public ResponseEntity<MessageResponse> updateReview(
            @PathVariable("review-id") String reviewId,
            @Valid @RequestBody UpdateTrainerReviewRequest request
    ) {
        return ResponseEntity.ok(clientReviewService.updateReview(reviewId, request));
    }
}
