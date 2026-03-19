package com.dev.quikkkk.modules.review.service.impl;

import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.mapper.ClientReviewMapper;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.review.service.IClientReviewService;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.repository.IClientProfileRepository;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientReviewServiceImpl implements IClientReviewService {
    private final IReviewRepository reviewRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final ClientReviewMapper clientReviewMapper;

    @Override
    public TrainerReviewResponse createReview(CreateTrainerReviewRequest request, String trainerId) {
        TrainerProfile trainer = trainerProfileRepository
                .findTrainerProfileById(trainerId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));

        ClientProfile client = clientProfileRepository
                .findByUserId(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        TrainerReview review = clientReviewMapper.toEntity(trainer, client, request);
        reviewRepository.save(review);

        return clientReviewMapper.toResponse(review);
    }
}
