package com.dev.quikkkk.modules.workout.service.impl;

import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.ResourceNotFoundException;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import com.dev.quikkkk.modules.workout.dto.response.WaitlistResponse;
import com.dev.quikkkk.modules.workout.entity.SessionWaitlist;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
import com.dev.quikkkk.modules.workout.enums.TrainingStatus;
import com.dev.quikkkk.modules.workout.enums.TrainingType;
import com.dev.quikkkk.modules.workout.enums.WaitlistStatus;
import com.dev.quikkkk.modules.workout.mapper.WaitlistMapper;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import com.dev.quikkkk.modules.workout.repository.IWaitlistRepository;
import com.dev.quikkkk.modules.workout.service.IWaitlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_IN_WAITLIST;
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
    private final WaitlistMapper waitlistMapper;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    public WaitlistResponse joinWaitlist(String sessionId) {
        TrainingSession session = findSessionById(sessionId);

        if (session.getStatus() != TrainingStatus.SCHEDULED) throw new BusinessException(SESSION_NOT_JOINABLE);
        if (session.getType() == TrainingType.PERSONAL) throw new BusinessException(WAITLIST_NOT_ALLOWED_FOR_PERSONAL);

        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Set<ClientProfile> clients = session.getClients();

        if (clients.size() < session.getMaxParticipants()) throw new BusinessException(SESSION_HAS_AVAILABLE_SLOTS);
        boolean isAlreadyInSession = clients.stream().anyMatch(c -> c.getId().equals(client.getId()));

        if (isAlreadyInSession) throw new BusinessException(CLIENT_ALREADY_JOINED_SESSION);
        boolean isAlreadyInWaitlist = waitlistRepository.existsBySessionIdAndClientIdAndStatus(
                sessionId, client.getId(), WaitlistStatus.WAITING
        );

        if (isAlreadyInWaitlist) throw new BusinessException(CLIENT_ALREADY_IN_WAITLIST);

        Integer maxPosition = waitlistRepository.findMaxPositionBySessionIdAndStatus(sessionId, WaitlistStatus.WAITING);
        int newPosition = (maxPosition == null ? 0 : maxPosition) + 1;

        SessionWaitlist waitlist = waitlistMapper.toEntity(session, client, newPosition);
        waitlistRepository.save(waitlist);

        log.info("Client {} joined waitlist for session {} at position {}", client.getId(), sessionId, newPosition);
        return waitlistMapper.toResponse(waitlist);
    }

    @Override
    public void leaveWaitlist(String sessionId) {
        findSessionById(sessionId);
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();

        SessionWaitlist waitlist = waitlistRepository.findBySessionIdAndClientIdAndStatus(
                sessionId, client.getId(), WaitlistStatus.WAITING
        ).orElseThrow(() -> new ResourceNotFoundException("You are not on the waiting list for this session"));

        int cancelledPosition = waitlist.getPosition();

        waitlist.setStatus(WaitlistStatus.CANCELLED);
        waitlistRepository.save(waitlist);

        List<SessionWaitlist> remainingWaitlist = waitlistRepository
                .findBySessionIdAndStatusAndPositionGreaterThanOrderByPositionAsc(
                        sessionId, WaitlistStatus.WAITING, cancelledPosition
                );

        for (SessionWaitlist entry : remainingWaitlist) entry.setPosition(entry.getPosition() - 1);
        waitlistRepository.saveAll(remainingWaitlist);

        log.info(
                "Client {} left waitlist for session {}. Shifted {} users up.",
                client.getId(), sessionId, remainingWaitlist.size()
        );
    }

    private TrainingSession findSessionById(String sessionId) {
        return trainingSessionRepository.findByIdWithPessimisticLock(sessionId)
                .orElseThrow(() -> new BusinessException(SESSION_NOT_FOUND));
    }
}
