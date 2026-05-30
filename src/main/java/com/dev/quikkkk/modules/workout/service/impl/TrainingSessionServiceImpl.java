package com.dev.quikkkk.modules.workout.service.impl;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.utils.PaginationUtils;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.auth.service.ISessionLockService;
import com.dev.quikkkk.modules.membership.entity.Membership;
import com.dev.quikkkk.modules.membership.enums.MembershipStatus;
import com.dev.quikkkk.modules.membership.enums.MembershipType;
import com.dev.quikkkk.modules.membership.repository.IMembershipRepository;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.repository.IClientProfileRepository;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import com.dev.quikkkk.modules.workout.dto.request.CheckInTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.dto.request.UpdateTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.dto.response.CheckInResponse;
import com.dev.quikkkk.modules.workout.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.modules.workout.entity.Attendance;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
import com.dev.quikkkk.modules.workout.mapper.TrainingSessionMapper;
import com.dev.quikkkk.modules.workout.repository.IAttendanceRepository;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import com.dev.quikkkk.modules.workout.service.ITrainingSessionService;
import com.dev.quikkkk.modules.workout.validator.TrainingSessionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.NO_ACTIVE_MEMBERSHIP;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.VISITS_LIMIT_REACHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingSessionServiceImpl implements ITrainingSessionService {
    private final ITrainingSessionRepository trainingSessionRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IAttendanceRepository attendanceRepository;
    private final IMembershipRepository membershipRepository;
    private final TrainingSessionMapper trainingSessionMapper;
    private final MessageMapper messageMapper;
    private final ISessionLockService sessionLockService;
    private final ClientProfileUtils clientProfileUtils;
    private final TrainingSessionValidator sessionValidator;

    @Override
    @Transactional
    @CacheEvict(value = "trainingSessions", allEntries = true)
    public TrainingSessionResponse createSession(CreateTrainingSessionRequest request) {
        TrainerProfile trainer = getCurrentTrainer();

        sessionValidator.validateTrainerIsActive(trainer);
        sessionValidator.validateSessionCreationRequest(request);
        sessionValidator.validateTrainerSessionOverlap(trainer, request);

        TrainingSession session = trainingSessionMapper.toEntity(request, trainer);
        trainingSessionRepository.save(session);

        return trainingSessionMapper.toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "lists",
            key = "'sessions:' + #page + ':' + #size + ':' + (#search != null ? #search : 'all')"
    )
    public PageResponse<TrainingSessionResponse> getTrainingSessions(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "startTime");
        Page<TrainingSession> sessionPage = trainingSessionRepository.findAllWithOptionalSearch(search, pageable);

        return PaginationUtils.toPageResponse(sessionPage, trainingSessionMapper::toResponse);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "trainingSessions", allEntries = true),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public TrainingSessionResponse updateSession(String sessionId, UpdateTrainingSessionRequest request) {
        TrainerProfile trainer = getCurrentTrainer();
        TrainingSession session = getSession(sessionId);

        sessionValidator.validateTrainerOwnership(session, trainer);
        sessionValidator.validateSessionIsEditable(session);
        sessionValidator.validatePersonalTrainingParticipants(session, request);

        trainingSessionMapper.updateSession(session, request);
        trainingSessionRepository.save(session);

        return trainingSessionMapper.toResponse(session);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Caching(evict = {
            @CacheEvict(value = "trainingSessions", key = "#sessionId"),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public MessageResponse joinToSession(String sessionId) {
        return sessionLockService.executeWithLock(sessionId, () -> {
            TrainingSession session = trainingSessionRepository
                    .findByIdWithPessimisticLock(sessionId)
                    .orElseThrow(() -> new BusinessException(SESSION_NOT_FOUND));

            sessionValidator.validateSessionJoinAvailability(session);
            ClientProfile client = clientProfileUtils.getCurrentClientProfile();

            sessionValidator.ensureClientNotAlreadyJoined(session, client);
            Membership membership = getActiveMembership(client);

            sessionValidator.validateMembershipForSession(membership, session);
            session.getClients().add(client);

            trainingSessionRepository.save(session);
            return messageMapper.message("Successfully joined training session");
        });
    }

    @Override
    @Transactional
    public CheckInResponse checkIn(String sessionId, CheckInTrainingSessionRequest request) {
        LocalDateTime now = LocalDateTime.now();

        TrainerProfile trainer = getCurrentTrainer();
        TrainingSession session = getSession(sessionId);

        sessionValidator.validateTrainerOwnership(session, trainer);
        sessionValidator.validateSessionForCheckIn(session, now);

        ClientProfile client = getClient(request.getClientId());

        sessionValidator.ensureClientJoinedSession(session, client);
        sessionValidator.ensureClientNotCheckedIn(client, sessionId);

        Membership membership = getActiveMembership(client);
        processMembershipCheckIn(membership, now);

        Attendance attendance = createAttendance(trainer, client, session, now);
        attendanceRepository.save(attendance);

        String visitsInfo = buildVisitsInfo(membership);
        return CheckInResponse.builder()
                .success(true)
                .sessionId(session.getId())
                .clientId(client.getId())
                .checkInTime(now)
                .message("Client successfully checked in. Visits left: " + visitsInfo)
                .build();
    }

    private Membership getActiveMembership(ClientProfile client) {
        Membership membership = membershipRepository
                .findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(NO_ACTIVE_MEMBERSHIP));

        sessionValidator.validateMembershipStatus(membership);
        return membership;
    }


    private void processMembershipCheckIn(Membership membership, LocalDateTime now) {
        if (membership.getType() == MembershipType.VISITS) {
            int updatedRows = membershipRepository.decrementVisits(membership.getId());
            if (updatedRows == 0) throw new BusinessException(VISITS_LIMIT_REACHED);

            if (membership.getVisitsLeft() != null) {
                membership.setVisitsLeft(membership.getVisitsLeft() - 1);
            }

            return;
        }

        sessionValidator.validateMembershipExpiration(membership, now);
    }

    private Attendance createAttendance(
            TrainerProfile trainer,
            ClientProfile client,
            TrainingSession session,
            LocalDateTime now
    ) {
        return Attendance.builder()
                .client(client)
                .session(session)
                .checkInTime(now)
                .createdBy(trainer.getId())
                .build();
    }

    private TrainerProfile getCurrentTrainer() {
        return trainerProfileRepository
                .findTrainerProfileByUserId(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }

    private TrainingSession getSession(String sessionId) {
        return trainingSessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new BusinessException(SESSION_NOT_FOUND));
    }

    private ClientProfile getClient(String clientId) {
        return clientProfileRepository
                .findById(clientId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
    }

    private String buildVisitsInfo(Membership membership) {
        if (membership.getType() != MembershipType.VISITS) return "unlimited";
        if (membership.getVisitsLeft() == null) return "0";
        return String.valueOf(membership.getVisitsLeft());
    }
}
