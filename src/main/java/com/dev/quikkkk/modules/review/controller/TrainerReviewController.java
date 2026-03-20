package com.dev.quikkkk.modules.review.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewSummaryResponse;
import com.dev.quikkkk.modules.review.service.ITrainerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER')")
public class TrainerReviewController {
    private final ITrainerReviewService trainerReviewService;

    @GetMapping("/trainers/{trainer-id}")
    public ResponseEntity<PageResponse<TrainerReviewResponse>> getReviewsByTrainerId(
            @PathVariable("trainer-id") String trainerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(trainerReviewService.getReviewsByTrainerId(trainerId, page, size));
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<TrainerReviewResponse>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(trainerReviewService.getMyReviews(page, size));
    }

    @GetMapping("/me/summary")
    public ResponseEntity<TrainerReviewSummaryResponse> getMyReviewSummary() {
        return ResponseEntity.ok(trainerReviewService.getMyReviewSummary());
    }
}
