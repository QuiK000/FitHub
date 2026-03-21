package com.dev.quikkkk.modules.review.service.impl;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.utils.PaginationUtils;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.mapper.ReviewMapper;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.review.service.IClientReviewService;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.repository.IClientProfileRepository;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.dev.quikkkk.core.enums.ErrorCode.CANNOT_REVIEW_SELF;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.NO_PRIOR_INTERACTION;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_IS_HIDDEN_AND_CANNOT_BE_EDITED;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientReviewServiceImpl implements IClientReviewService {
    private final IReviewRepository reviewRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final ITrainingSessionRepository trainingSessionRepository;
    private final ReviewMapper reviewMapper;
    private final MessageMapper messageMapper;

    @Override
    public TrainerReviewResponse createReview(CreateTrainerReviewRequest request, String trainerId) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        ClientProfile client = clientProfileRepository
                .findByUserId(currentUserId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        TrainerProfile trainer = trainerProfileRepository
                .findTrainerProfileById(trainerId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));

        if (trainer.getUser().getId().equals(currentUserId)) {
            log.warn("User {} attempted to review themselves as trainer {}", currentUserId, trainerId);
            throw new BusinessException(CANNOT_REVIEW_SELF);
        }

        if (reviewRepository.existsByTrainerIdAndReviewerId(trainer.getId(), client.getId())) {
            log.warn("Client {} already reviewed trainer {}", client.getId(), trainerId);
            throw new BusinessException(REVIEW_ALREADY_EXISTS);
        }

        boolean hasCompletedSessions = trainingSessionRepository
                .existsCompletedSessionWithTrainer(trainer.getId(), client.getId());

        if (!hasCompletedSessions) {
            log.warn("Client {} tried to review trainer {} without completed sessions", client.getId(), trainerId);
            throw new BusinessException(NO_PRIOR_INTERACTION);
        }

        TrainerReview review = reviewMapper.toEntity(trainer, client, request);

        review.setVisible(true);
        review.setEdited(false);
        review.setEditedAt(null);

        reviewRepository.save(review);
        log.info("Client {} successfully created a review for trainer {}", client.getId(), trainerId);

        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TrainerReviewResponse> getReviews(int page, int size) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        ClientProfile client = clientProfileRepository
                .findByUserId(currentUserId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<TrainerReview> trainerReviewPage = reviewRepository.findAllByReviewerId(client.getId(), pageable);

        return PaginationUtils.toPageResponse(trainerReviewPage, reviewMapper::toResponse);
    }

    @Override
    public MessageResponse updateReview(String reviewId, UpdateTrainerReviewRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        ClientProfile client = clientProfileRepository
                .findByUserId(currentUserId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        TrainerReview review = reviewRepository.findByIdAndReviewerId(reviewId, client.getId())
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));

        if (!review.isVisible()) throw new BusinessException(REVIEW_IS_HIDDEN_AND_CANNOT_BE_EDITED);
        reviewMapper.update(request, review);

        review.setEdited(true);
        review.setEditedAt(LocalDateTime.now());
        review.setLastModifiedBy(client.getId());

        return messageMapper.message("Review successfully updated");
    }
}
