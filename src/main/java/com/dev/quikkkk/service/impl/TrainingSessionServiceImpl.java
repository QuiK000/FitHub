package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.TrainingSession;
import com.dev.quikkkk.enums.TrainingType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.TrainingSessionMapper;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.repository.ITrainingSessionRepository;
import com.dev.quikkkk.service.ITrainingSessionService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.dev.quikkkk.enums.ErrorCode.GROUP_TRAINING_MIN_TWO_PARTICIPANTS;
import static com.dev.quikkkk.enums.ErrorCode.PERSONAL_TRAINING_MAX_ONE_PARTICIPANT;
import static com.dev.quikkkk.enums.ErrorCode.START_TIME_IN_PAST;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingSessionServiceImpl implements ITrainingSessionService {
    private final ITrainingSessionRepository trainingSessionRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final TrainingSessionMapper trainingSessionMapper;

    @Override
    @Transactional
    public TrainingSessionResponse createSession(CreateTrainingSessionRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        TrainerProfile trainer = trainerProfileRepository
                .findTrainerProfileByUserId(userId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));

        if (!trainer.isActive()) throw new BusinessException(TRAINER_PROFILE_DEACTIVATED);
        TrainingSession session = trainingSessionMapper.toEntity(request, trainer);

        if (session.getStartTime().isBefore(LocalDateTime.now()))
            throw new BusinessException(START_TIME_IN_PAST);

        if (session.getType().equals(TrainingType.PERSONAL) && session.getMaxParticipants() > 1)
            throw new BusinessException(PERSONAL_TRAINING_MAX_ONE_PARTICIPANT);

        if (session.getType().equals(TrainingType.GROUP) && session.getMaxParticipants() <= 1)
            throw new BusinessException(GROUP_TRAINING_MIN_TWO_PARTICIPANTS);

        trainingSessionRepository.save(session);
        return trainingSessionMapper.toResponse(session);
    }
}
