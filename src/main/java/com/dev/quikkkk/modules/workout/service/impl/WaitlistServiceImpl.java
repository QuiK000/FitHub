package com.dev.quikkkk.modules.workout.service.impl;

import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.workout.dto.response.WaitlistResponse;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
import com.dev.quikkkk.modules.workout.enums.TrainingStatus;
import com.dev.quikkkk.modules.workout.enums.TrainingType;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import com.dev.quikkkk.modules.workout.repository.IWaitlistRepository;
import com.dev.quikkkk.modules.workout.service.IWaitlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_JOINED_SESSION;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_HAS_AVAILABLE_SLOTS;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_JOINABLE;
import static com.dev.quikkkk.core.enums.ErrorCode.WAITLIST_NOT_ALLOWED_FOR_PERSONAL;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WaitlistServiceImpl implements IWaitlistService {
    private final IWaitlistRepository waitlistRepository;
    private final ITrainingSessionRepository trainingSessionRepository;

    @Override
    public WaitlistResponse joinWaitlist(String sessionId, String userId) {
        TrainingSession session = findSessionById(sessionId);

        if (session.getStatus() != TrainingStatus.SCHEDULED) throw new BusinessException(SESSION_NOT_JOINABLE);
        if (session.getType() == TrainingType.PERSONAL) throw new BusinessException(WAITLIST_NOT_ALLOWED_FOR_PERSONAL);

        Set<ClientProfile> clients = session.getClients();

        if (clients.size() < session.getMaxParticipants()) throw new BusinessException(SESSION_HAS_AVAILABLE_SLOTS);
        boolean isAlreadyInSession = clients.stream().anyMatch(client -> client.getId().equals(userId));

        if (isAlreadyInSession) throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);
        return null;
    }

    @Override
    public WaitlistResponse leaveWaitlist(String sessionId, String userId) {
        TrainingSession session = findSessionById(sessionId);
        return null;
    }

    private TrainingSession findSessionById(String sessionId) {
        return trainingSessionRepository.findByIdWithPessimisticLock(sessionId)
                .orElseThrow(() -> new BusinessException(SESSION_NOT_FOUND));
    }
}
