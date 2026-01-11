package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CheckInTrainingSessionRequest;
import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.request.UpdateTrainingSessionRequest;
import com.dev.quikkkk.dto.response.CheckInResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.entity.Attendance;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.TrainingSession;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.TrainingStatus;
import com.dev.quikkkk.enums.TrainingType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.TrainingSessionMapper;
import com.dev.quikkkk.repository.IAttendanceRepository;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.repository.ITrainingSessionRepository;
import com.dev.quikkkk.service.ITrainingSessionService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_ALREADY_JOINED_SESSION;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.GROUP_TRAINING_MIN_TWO_PARTICIPANTS;
import static com.dev.quikkkk.enums.ErrorCode.NO_ACTIVE_MEMBERSHIP;
import static com.dev.quikkkk.enums.ErrorCode.PERSONAL_TRAINING_MAX_ONE_PARTICIPANT;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_IS_FULL;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_NOT_JOINABLE;
import static com.dev.quikkkk.enums.ErrorCode.START_TIME_IN_PAST;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.UNAUTHORIZED_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingSessionServiceImpl implements ITrainingSessionService {
    private final ITrainingSessionRepository trainingSessionRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IAttendanceRepository attendanceRepository;
    private final TrainingSessionMapper trainingSessionMapper;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public TrainingSessionResponse createSession(CreateTrainingSessionRequest request) {
        TrainerProfile trainer = findTrainerProfileByUserId();

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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TrainingSessionResponse> getTrainingSessions(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "startTime");
        Page<TrainingSession> sessionPage = trainingSessionRepository.findAllWithOptionalSearch(search, pageable);

        return PaginationUtils.toPageResponse(sessionPage, trainingSessionMapper::toResponse);
    }

    @Override
    @Transactional
    public TrainingSessionResponse updateSession(String sessionId, UpdateTrainingSessionRequest request) {
        TrainerProfile trainer = findTrainerProfileByUserId();
        TrainingSession session = findSessionById(sessionId);

        if (!session.getTrainer().equals(trainer)) throw new BusinessException(UNAUTHORIZED_USER);
        if (session.getType().equals(TrainingType.PERSONAL) && request.getMaxParticipants() > 1)
            throw new BusinessException(PERSONAL_TRAINING_MAX_ONE_PARTICIPANT);

        trainingSessionMapper.updateSession(session, request);
        trainingSessionRepository.save(session);

        return trainingSessionMapper.toResponse(session);
    }

    @Override
    @Transactional
    public MessageResponse joinToSession(String sessionId) {
        String userId = SecurityUtils.getCurrentUserId();
        TrainingSession session = findSessionById(sessionId);

        if (!session.getStatus().equals(TrainingStatus.SCHEDULED)) throw new BusinessException(SESSION_NOT_JOINABLE);
        if (session.getStartTime().isBefore(LocalDateTime.now())) throw new BusinessException(START_TIME_IN_PAST);

        ClientProfile client = clientProfileRepository
                .findByUserIdAndActiveMembership(userId, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(NO_ACTIVE_MEMBERSHIP));

        if (session.getClients().contains(client)) throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);
        if (session.getClients().size() >= session.getMaxParticipants()) throw new BusinessException(SESSION_IS_FULL);

        session.getClients().add(client);
        trainingSessionRepository.save(session);

        return messageMapper.message("Successfully joined training session");
    }

    @Override
    @Transactional
    public CheckInResponse checkIn(String sessionId, CheckInTrainingSessionRequest request) {
        TrainerProfile trainer = findTrainerProfileByUserId();
        TrainingSession session = findSessionById(sessionId);

        if (!session.getTrainer().getId().equals(trainer.getId())) throw new BusinessException(UNAUTHORIZED_USER);
        if (!session.getStatus().equals(TrainingStatus.SCHEDULED)) throw new BusinessException(SESSION_NOT_JOINABLE);

        ClientProfile client = clientProfileRepository.findById(request.getClientId())
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        if (!session.getClients().contains(client)) throw new BusinessException(UNAUTHORIZED_USER);
        if (LocalDateTime.now().isBefore(session.getStartTime().minusMinutes(10)))
            throw new BusinessException(START_TIME_IN_PAST);

        if (LocalDateTime.now().isAfter(session.getEndTime())) throw new BusinessException(START_TIME_IN_PAST);
        boolean alreadyCheckedIn = attendanceRepository.existsByClientIdAndSessionId(client.getId(), session.getId());
        if (alreadyCheckedIn) throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);

        attendanceRepository.save(
                Attendance.builder()
                        .client(client)
                        .session(session)
                        .checkInTime(LocalDateTime.now())
                        .createdBy(session.getCreatedBy())
                        .build()
        );

        return CheckInResponse.builder()
                .success(true)
                .sessionId(session.getId())
                .clientId(client.getId())
                .checkInTime(LocalDateTime.now())
                .message("Client successfully checked in")
                .build();
    }

    private TrainerProfile findTrainerProfileByUserId() {
        String userId = SecurityUtils.getCurrentUserId();
        return trainerProfileRepository
                .findTrainerProfileByUserId(userId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }

    private TrainingSession findSessionById(String sessionId) {
        return trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(SESSION_NOT_FOUND));
    }
}
