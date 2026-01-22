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
import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.TrainingSession;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
import com.dev.quikkkk.enums.TrainingStatus;
import com.dev.quikkkk.enums.TrainingType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.TrainingSessionMapper;
import com.dev.quikkkk.repository.IAttendanceRepository;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.repository.ITrainingSessionRepository;
import com.dev.quikkkk.service.ISessionLockService;
import com.dev.quikkkk.service.ITrainingSessionService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_ALREADY_JOINED_SESSION;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.GROUP_TRAINING_MIN_TWO_PARTICIPANTS;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_EXPIRED;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_FROZEN;
import static com.dev.quikkkk.enums.ErrorCode.NO_ACTIVE_MEMBERSHIP;
import static com.dev.quikkkk.enums.ErrorCode.PERSONAL_TRAINING_MAX_ONE_PARTICIPANT;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_ALREADY_FINISHED;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_CHECKIN_TOO_EARLY;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_IS_FULL;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_NOT_JOINABLE;
import static com.dev.quikkkk.enums.ErrorCode.START_TIME_IN_PAST;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_SESSION_OVERLAP;
import static com.dev.quikkkk.enums.ErrorCode.UNAUTHORIZED_USER;
import static com.dev.quikkkk.enums.ErrorCode.VISITS_LIMIT_REACHED;

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

    @Override
    @Transactional
    @CacheEvict(value = "trainingSessions", allEntries = true)
    public TrainingSessionResponse createSession(CreateTrainingSessionRequest request) {
        log.info("Create session request: {}", request);
        TrainerProfile trainer = findTrainerProfileByUserId();
        boolean hasOverlap = trainingSessionRepository.hasOverlappingSession(
                trainer.getId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (hasOverlap) throw new BusinessException(TRAINER_SESSION_OVERLAP);
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
    @Cacheable(
            value = "lists",
            key = "'sessions:' + #page + ':' + #size + ':' + (#search != null ? #search : 'all')"
    )
    public PageResponse<TrainingSessionResponse> getTrainingSessions(int page, int size, String search) {
        log.info("Fetching training sessions page, size, search: {}, {}, {}", page, size, search);
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
        log.info("Update session with id: {}", sessionId);
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
    @Caching(evict = {
            @CacheEvict(value = "trainingSessions", key = "#sessionId"),
            @CacheEvict(value = "lists", allEntries = true)
    })
    public MessageResponse joinToSession(String sessionId) {
        log.info("Join session with id: {}", sessionId);
        return sessionLockService.executeWithLock(sessionId, () -> {
            String userId = SecurityUtils.getCurrentUserId();
            TrainingSession session = findSessionById(sessionId);

            if (!session.getStatus().equals(TrainingStatus.SCHEDULED))
                throw new BusinessException(SESSION_NOT_JOINABLE);
            if (session.getStartTime().isBefore(LocalDateTime.now())) throw new BusinessException(START_TIME_IN_PAST);

            ClientProfile client = clientProfileRepository
                    .findByUserIdAndActiveMembership(userId, MembershipStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException(NO_ACTIVE_MEMBERSHIP));

            if (session.getClients().contains(client)) throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);
            if (session.getClients().size() >= session.getMaxParticipants())
                throw new BusinessException(SESSION_IS_FULL);

            session.getClients().add(client);
            trainingSessionRepository.save(session);

            return messageMapper.message("Successfully joined training session");
        });
    }

    @Override
    @Transactional
    public CheckInResponse checkIn(String sessionId, CheckInTrainingSessionRequest request) {
        log.info("Check-in: sessionId={}, clientId={}", sessionId, request.getClientId());
        LocalDateTime now = LocalDateTime.now();

        TrainerProfile trainer = findTrainerProfileByUserId();
        TrainingSession session = findSessionById(sessionId);

        if (!session.getTrainer().getId().equals(trainer.getId())) throw new BusinessException(UNAUTHORIZED_USER);
        if (!session.getStatus().equals(TrainingStatus.SCHEDULED)) throw new BusinessException(SESSION_NOT_JOINABLE);

        if (now.isBefore(session.getStartTime().minusMinutes(10)))
            throw new BusinessException(SESSION_CHECKIN_TOO_EARLY);
        if (now.isAfter(session.getEndTime())) throw new BusinessException(SESSION_ALREADY_FINISHED);

        ClientProfile client = clientProfileRepository.findById(request.getClientId())
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        if (!trainingSessionRepository.existsClientInSession(session.getId(), client.getId()))
            throw new BusinessException(CLIENT_PROFILE_NOT_FOUND);

        if (attendanceRepository.existsByClientIdAndSessionId(client.getId(), sessionId))
            throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);

        Optional<Membership> activeMembership = membershipRepository.findMembershipByClientIdAndStatus(
                client.getId(),
                MembershipStatus.ACTIVE
        );

        if (activeMembership.isEmpty()) throw new BusinessException(NO_ACTIVE_MEMBERSHIP);
        Membership membership = activeMembership.get();

        if (membership.getStatus() == MembershipStatus.FROZEN) throw new BusinessException(MEMBERSHIP_FROZEN);

        if (membership.getType() == MembershipType.VISITS) {
            if (membership.getVisitsLeft() == null || membership.getVisitsLeft() <= 0) {
                throw new BusinessException(VISITS_LIMIT_REACHED);
            }

            membership.setVisitsLeft(membership.getVisitsLeft() - 1);
            membershipRepository.save(membership);
        }

        if (membership.getType() != MembershipType.VISITS) {
            if (membership.getEndDate() != null && membership.getEndDate().isBefore(now)) {
                throw new BusinessException(MEMBERSHIP_EXPIRED);
            }
        }

        Attendance attendance = Attendance.builder()
                .client(client)
                .session(session)
                .checkInTime(now)
                .createdBy(trainer.getId())
                .build();

        attendanceRepository.save(attendance);

        return CheckInResponse.builder()
                .success(true)
                .sessionId(session.getId())
                .clientId(client.getId())
                .checkInTime(now)
                .message("Client successfully checked in. Visits left: " +
                        (membership.getVisitsLeft() != null ? membership.getVisitsLeft() : "unlimited")
                )
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
