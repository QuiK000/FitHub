package com.dev.quikkkk.modules.review.service.impl;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.utils.PaginationUtils;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewSummaryResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.mapper.ReviewMapper;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.review.repository.projection.IReviewAggregationProjection;
import com.dev.quikkkk.modules.review.service.ITrainerReviewService;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrainerReviewServiceImpl implements ITrainerReviewService {
    private final IReviewRepository reviewRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public PageResponse<TrainerReviewResponse> getReviewsByTrainerId(String trainerId, int page, int size) {
        trainerProfileRepository.findTrainerProfileById(trainerId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<TrainerReview> trainerReviewPage = reviewRepository.findAllByTrainerId(trainerId, pageable);

        return PaginationUtils.toPageResponse(trainerReviewPage, reviewMapper::toResponse);
    }

    @Override
    public PageResponse<TrainerReviewResponse> getMyReviews(int page, int size) {
        return getReviewsByTrainerId(getCurrentTrainerProfile().getId(), page, size);
    }

    @Override
    public TrainerReviewSummaryResponse getMyReviewSummary() {
        String trainerId = getCurrentTrainerProfile().getId();
        IReviewAggregationProjection stats = reviewRepository.getReviewAggregationByTrainerId(trainerId);
        List<TrainerReview> recentReviews = List.of();

        if (stats != null && stats.getTotalReviews() != null && stats.getTotalReviews() > 0) {
            recentReviews = reviewRepository.findRecentReviewsWithClientByTrainerId(
                    trainerId, PageRequest.of(0, 5)
            );
        }

        return reviewMapper.toSummaryResponse(trainerId, stats, recentReviews);
    }

    private TrainerProfile getCurrentTrainerProfile() {
        return trainerProfileRepository.findTrainerProfileByUserId(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }
}
