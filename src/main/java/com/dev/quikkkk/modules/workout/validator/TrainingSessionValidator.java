package com.dev.quikkkk.modules.workout.validator;

import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.membership.entity.Membership;
import com.dev.quikkkk.modules.membership.enums.MembershipStatus;
import com.dev.quikkkk.modules.membership.enums.MembershipType;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.workout.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.dto.request.UpdateTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
import com.dev.quikkkk.modules.workout.enums.TrainingStatus;
import com.dev.quikkkk.modules.workout.enums.TrainingType;
import com.dev.quikkkk.modules.workout.repository.IAttendanceRepository;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_CHECKED_IN;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_JOINED_SESSION;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.GROUP_TRAINING_MIN_TWO_PARTICIPANTS;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_EXPIRED;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_FROZEN;
import static com.dev.quikkkk.core.enums.ErrorCode.PERSONAL_TRAINING_MAX_ONE_PARTICIPANT;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_ALREADY_FINISHED;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_CHECKIN_TOO_EARLY;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_CLOSED;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_IS_FULL;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_JOINABLE;
import static com.dev.quikkkk.core.enums.ErrorCode.START_TIME_IN_PAST;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_DEACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_SESSION_OVERLAP;
import static com.dev.quikkkk.core.enums.ErrorCode.UNAUTHORIZED_USER;
import static com.dev.quikkkk.core.enums.ErrorCode.VISITS_LIMIT_REACHED;

@Component
@RequiredArgsConstructor
public class TrainingSessionValidator {
    private static final int CHECK_IN_WINDOW_MINUTES = 30;

    private final ITrainingSessionRepository trainingSessionRepository;
    private final IAttendanceRepository attendanceRepository;

    public void validateSessionCreationRequest(CreateTrainingSessionRequest request) {
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(START_TIME_IN_PAST);
        }

        if (request.getType() == TrainingType.PERSONAL && request.getMaxParticipants() > 1) {
            throw new BusinessException(PERSONAL_TRAINING_MAX_ONE_PARTICIPANT);
        }

        if (request.getType() == TrainingType.GROUP && request.getMaxParticipants() <= 1) {
            throw new BusinessException(GROUP_TRAINING_MIN_TWO_PARTICIPANTS);
        }
    }


    public void validateTrainerSessionOverlap(TrainerProfile trainer, CreateTrainingSessionRequest request) {
        boolean hasOverlap = trainingSessionRepository.hasOverlappingSession(
                trainer.getId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (hasOverlap) {
            throw new BusinessException(TRAINER_SESSION_OVERLAP);
        }
    }

    public void validateSessionJoinAvailability(TrainingSession session) {
        if (session.getStatus() != TrainingStatus.SCHEDULED) {
            throw new BusinessException(SESSION_NOT_JOINABLE);
        }

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(START_TIME_IN_PAST);
        }

        validateTrainerIsActive(session.getTrainer());
        if (session.getClients().size() >= session.getMaxParticipants()) {
            throw new BusinessException(SESSION_IS_FULL);
        }
    }

    public void validateSessionForCheckIn(TrainingSession session, LocalDateTime now) {
        if (session.getStatus() != TrainingStatus.SCHEDULED) {
            throw new BusinessException(SESSION_NOT_JOINABLE);
        }

        if (now.isBefore(session.getStartTime().minusMinutes(CHECK_IN_WINDOW_MINUTES))) {
            throw new BusinessException(SESSION_CHECKIN_TOO_EARLY);
        }

        if (now.isAfter(session.getEndTime())) {
            throw new BusinessException(SESSION_ALREADY_FINISHED);
        }
    }

    public void validateSessionIsEditable(TrainingSession session) {
        if (session.getStatus() == TrainingStatus.CANCELLED || session.getStatus() == TrainingStatus.COMPLETED) {
            throw new BusinessException(SESSION_CLOSED);
        }
    }

    public void validatePersonalTrainingParticipants(TrainingSession session, UpdateTrainingSessionRequest request) {
        if (session.getType() == TrainingType.PERSONAL
                && request.getMaxParticipants() != null
                && request.getMaxParticipants() > 1
        ) throw new BusinessException(PERSONAL_TRAINING_MAX_ONE_PARTICIPANT);
    }

    public void validateMembershipStatus(Membership membership) {
        if (membership.getStatus() == MembershipStatus.FROZEN) {
            throw new BusinessException(MEMBERSHIP_FROZEN);
        }
    }

    public void validateMembershipForSession(Membership membership, TrainingSession session) {
        if (membership.getType() == MembershipType.VISITS) {
            if (membership.getVisitsLeft() == null || membership.getVisitsLeft() <= 0) {
                throw new BusinessException(VISITS_LIMIT_REACHED);
            }
            return;
        }
        validateMembershipExpiration(membership, session.getStartTime());
    }

    public void validateMembershipExpiration(Membership membership, LocalDateTime referenceTime) {
        if (membership.getEndDate() != null && membership.getEndDate().isBefore(referenceTime)) {
            throw new BusinessException(MEMBERSHIP_EXPIRED);
        }
    }

    public void ensureClientNotAlreadyJoined(TrainingSession session, ClientProfile client) {
        boolean alreadyJoined = session.getClients()
                .stream()
                .anyMatch(c -> c.getId().equals(client.getId()));

        if (alreadyJoined) {
            throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);
        }
    }

    public void ensureClientJoinedSession(TrainingSession session, ClientProfile client) {
        boolean exists = trainingSessionRepository.existsClientInSession(session.getId(), client.getId());

        if (!exists) {
            throw new BusinessException(CLIENT_PROFILE_NOT_FOUND);
        }
    }

    public void ensureClientNotCheckedIn(ClientProfile client, String sessionId) {
        boolean alreadyCheckedIn = attendanceRepository.existsByClientIdAndSessionId(client.getId(), sessionId);

        if (alreadyCheckedIn) {
            throw new BusinessException(CLIENT_ALREADY_CHECKED_IN);
        }
    }

    public void validateTrainerOwnership(TrainingSession session, TrainerProfile trainer) {
        if (!session.getTrainer().getId().equals(trainer.getId())) {
            throw new BusinessException(UNAUTHORIZED_USER);
        }
    }

    public void validateTrainerIsActive(TrainerProfile trainer) {
        if (!trainer.isActive()) {
            throw new BusinessException(TRAINER_PROFILE_DEACTIVATED);
        }
    }
}
