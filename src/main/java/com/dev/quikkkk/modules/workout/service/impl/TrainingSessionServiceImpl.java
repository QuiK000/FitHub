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
import com.dev.quikkkk.modules.workout.enums.TrainingStatus;
import com.dev.quikkkk.modules.workout.enums.TrainingType;
import com.dev.quikkkk.modules.workout.mapper.TrainingSessionMapper;
import com.dev.quikkkk.modules.workout.repository.IAttendanceRepository;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import com.dev.quikkkk.modules.workout.service.ITrainingSessionService;
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

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_CHECKED_IN;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_JOINED_SESSION;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.GROUP_TRAINING_MIN_TWO_PARTICIPANTS;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_EXPIRED;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_FROZEN;
import static com.dev.quikkkk.core.enums.ErrorCode.NO_ACTIVE_MEMBERSHIP;
import static com.dev.quikkkk.core.enums.ErrorCode.PERSONAL_TRAINING_MAX_ONE_PARTICIPANT;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_ALREADY_FINISHED;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_CHECKIN_TOO_EARLY;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_CLOSED;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_IS_FULL;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_JOINABLE;
import static com.dev.quikkkk.core.enums.ErrorCode.START_TIME_IN_PAST;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_SESSION_OVERLAP;
import static com.dev.quikkkk.core.enums.ErrorCode.UNAUTHORIZED_USER;
import static com.dev.quikkkk.core.enums.ErrorCode.VISITS_LIMIT_REACHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingSessionServiceImpl implements ITrainingSessionService {
    private static final int CHECK_IN_WINDOW_MINUTES = 30;

    private final ITrainingSessionRepository trainingSessionRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IAttendanceRepository attendanceRepository;
    private final IMembershipRepository membershipRepository;
    private final TrainingSessionMapper trainingSessionMapper;
    private final MessageMapper messageMapper;
    private final ISessionLockService sessionLockService;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    @Transactional
    @CacheEvict(value = "trainingSessions", allEntries = true)
    public TrainingSessionResponse createSession(CreateTrainingSessionRequest request) {
        TrainerProfile trainer = getCurrentTrainer();

        validateTrainerIsActive(trainer);
        validateSessionCreationRequest(request);
        validateTrainerSessionOverlap(trainer, request);

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

        validateTrainerOwnership(session, trainer);
        validateSessionIsEditable(session);
        validatePersonalTrainingParticipants(session, request);

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

            validateSessionJoinAvailability(session);
            ClientProfile client = clientProfileUtils.getCurrentClientProfile();

            ensureClientNotAlreadyJoined(session, client);
            Membership membership = getActiveMembership(client);

            validateMembershipForSession(membership, session);
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

        validateTrainerOwnership(session, trainer);
        validateSessionForCheckIn(session, now);

        ClientProfile client = getClient(request.getClientId());

        ensureClientJoinedSession(session, client);
        ensureClientNotCheckedIn(client, sessionId);

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

    private void validateSessionCreationRequest(CreateTrainingSessionRequest request) {
        if (request.getStartTime().isBefore(LocalDateTime.now())) throw new BusinessException(START_TIME_IN_PAST);
        if (request.getType() == TrainingType.PERSONAL && request.getMaxParticipants() > 1)
            throw new BusinessException(PERSONAL_TRAINING_MAX_ONE_PARTICIPANT);

        if (request.getType() == TrainingType.GROUP && request.getMaxParticipants() <= 1) {
            throw new BusinessException(GROUP_TRAINING_MIN_TWO_PARTICIPANTS);
        }
    }

    private void validateTrainerSessionOverlap(TrainerProfile trainer, CreateTrainingSessionRequest request) {
        boolean hasOverlap = trainingSessionRepository.hasOverlappingSession(
                trainer.getId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (hasOverlap) {
            throw new BusinessException(TRAINER_SESSION_OVERLAP);
        }
    }

    private void validateSessionJoinAvailability(TrainingSession session) {
        if (session.getStatus() != TrainingStatus.SCHEDULED) throw new BusinessException(SESSION_NOT_JOINABLE);
        if (session.getStartTime().isBefore(LocalDateTime.now())) throw new BusinessException(START_TIME_IN_PAST);

        validateTrainerIsActive(session.getTrainer());
        if (session.getClients().size() >= session.getMaxParticipants()) throw new BusinessException(SESSION_IS_FULL);
    }

    private void validateSessionForCheckIn(TrainingSession session, LocalDateTime now) {
        if (session.getStatus() != TrainingStatus.SCHEDULED) throw new BusinessException(SESSION_NOT_JOINABLE);
        if (now.isBefore(session.getStartTime().minusMinutes(CHECK_IN_WINDOW_MINUTES)))
            throw new BusinessException(SESSION_CHECKIN_TOO_EARLY);

        if (now.isAfter(session.getEndTime())) throw new BusinessException(SESSION_ALREADY_FINISHED);
    }

    private void validateSessionIsEditable(TrainingSession session) {
        if (session.getStatus() == TrainingStatus.CANCELLED || session.getStatus() == TrainingStatus.COMPLETED) {
            throw new BusinessException(SESSION_CLOSED);
        }
    }

    private void validatePersonalTrainingParticipants(TrainingSession session, UpdateTrainingSessionRequest request) {
        if (session.getType() == TrainingType.PERSONAL
                && request.getMaxParticipants() != null
                && request.getMaxParticipants() > 1
        ) throw new BusinessException(PERSONAL_TRAINING_MAX_ONE_PARTICIPANT);
    }

    private Membership getActiveMembership(ClientProfile client) {
        Membership membership = membershipRepository
                .findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(NO_ACTIVE_MEMBERSHIP));

        validateMembershipStatus(membership);
        return membership;
    }

    private void validateMembershipStatus(Membership membership) {
        if (membership.getStatus() == MembershipStatus.FROZEN) {
            throw new BusinessException(MEMBERSHIP_FROZEN);
        }
    }

    private void validateMembershipForSession(Membership membership, TrainingSession session) {
        if (membership.getType() == MembershipType.VISITS) {
            if (membership.getVisitsLeft() == null || membership.getVisitsLeft() <= 0)
                throw new BusinessException(VISITS_LIMIT_REACHED);
            return;
        }

        validateMembershipExpiration(membership, session.getStartTime());
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

        validateMembershipExpiration(membership, now);
    }

    private void validateMembershipExpiration(Membership membership, LocalDateTime referenceTime) {
        if (membership.getEndDate() != null && membership.getEndDate().isBefore(referenceTime)) {
            throw new BusinessException(MEMBERSHIP_EXPIRED);
        }
    }

    private void ensureClientNotAlreadyJoined(TrainingSession session, ClientProfile client) {
        boolean alreadyJoined = session.getClients()
                .stream()
                .anyMatch(c -> c.getId().equals(client.getId()));

        if (alreadyJoined) {
            throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);
        }
    }

    private void ensureClientJoinedSession(TrainingSession session, ClientProfile client) {
        boolean exists = trainingSessionRepository.existsClientInSession(session.getId(), client.getId());

        if (!exists) {
            throw new BusinessException(CLIENT_PROFILE_NOT_FOUND);
        }
    }

    private void ensureClientNotCheckedIn(ClientProfile client, String sessionId) {
        boolean alreadyCheckedIn = attendanceRepository.existsByClientIdAndSessionId(client.getId(), sessionId);

        if (alreadyCheckedIn) {
            throw new BusinessException(CLIENT_ALREADY_CHECKED_IN);
        }
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

    private void validateTrainerOwnership(TrainingSession session, TrainerProfile trainer) {
        if (!session.getTrainer().getId().equals(trainer.getId())) {
            throw new BusinessException(UNAUTHORIZED_USER);
        }
    }

    private void validateTrainerIsActive(TrainerProfile trainer) {
        if (!trainer.isActive()) {
            throw new BusinessException(TRAINER_PROFILE_DEACTIVATED);
        }
    }

    private String buildVisitsInfo(Membership membership) {
        if (membership.getType() != MembershipType.VISITS) return "unlimited";
        if (membership.getVisitsLeft() == null) return "0";
        return String.valueOf(membership.getVisitsLeft());
    }
}
