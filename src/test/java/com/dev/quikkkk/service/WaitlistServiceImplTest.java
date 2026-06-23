package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
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
import com.dev.quikkkk.modules.workout.service.impl.WaitlistServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_IN_WAITLIST;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_ALREADY_JOINED_SESSION;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_HAS_AVAILABLE_SLOTS;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_JOINABLE;
import static com.dev.quikkkk.core.enums.ErrorCode.WAITLIST_NOT_ALLOWED_FOR_PERSONAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WaitlistService Tests")
class WaitlistServiceImplTest {

    @Mock
    private IWaitlistRepository waitlistRepository;
    @Mock
    private ITrainingSessionRepository trainingSessionRepository;
    @Mock
    private WaitlistMapper waitlistMapper;
    @Mock
    private ClientProfileUtils clientProfileUtils;

    @InjectMocks
    private WaitlistServiceImpl waitlistService;

    @Test
    @DisplayName("Should join waitlist successfully")
    void joinWaitlist_WithFullSession_ReturnsResponse() {
        ClientProfile client = createClient();
        TrainingSession session = createFullSession();
        SessionWaitlist waitlist = SessionWaitlist.builder().id(UUID.randomUUID().toString()).position(1).build();
        WaitlistResponse expected = WaitlistResponse.builder().id(waitlist.getId()).position(1).build();

        when(trainingSessionRepository.findByIdWithPessimisticLock(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(waitlistRepository.existsBySessionIdAndClientIdAndStatus(session.getId(), client.getId(), WaitlistStatus.WAITING))
                .thenReturn(false);
        when(waitlistRepository.findMaxPositionBySessionIdAndStatus(session.getId(), WaitlistStatus.WAITING)).thenReturn(null);
        when(waitlistMapper.toEntity(session, client, 1)).thenReturn(waitlist);
        when(waitlistMapper.toResponse(waitlist)).thenReturn(expected);

        WaitlistResponse response = waitlistService.joinWaitlist(session.getId());

        assertThat(response).isNotNull();
        assertThat(response.getPosition()).isEqualTo(1);
        verify(waitlistRepository).save(waitlist);
    }

    @Test
    @DisplayName("Should throw exception when session not found")
    void joinWaitlist_WhenSessionNotFound_ThrowsBusinessException() {
        when(trainingSessionRepository.findByIdWithPessimisticLock("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> waitlistService.joinWaitlist("nonexistent"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SESSION_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw exception when session is not scheduled")
    void joinWaitlist_WhenSessionNotScheduled_ThrowsBusinessException() {
        com.dev.quikkkk.modules.user.entity.TrainerProfile trainer = com.dev.quikkkk.modules.user.entity.TrainerProfile.builder()
                .id(UUID.randomUUID().toString()).active(true).build();
        TrainingSession session = TrainingSession.builder()
                .id(UUID.randomUUID().toString())
                .status(TrainingStatus.COMPLETED)
                .type(TrainingType.GROUP)
                .maxParticipants(10)
                .clients(new HashSet<>())
                .build();

        when(trainingSessionRepository.findByIdWithPessimisticLock(session.getId())).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> waitlistService.joinWaitlist(session.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SESSION_NOT_JOINABLE);
    }

    @Test
    @DisplayName("Should throw exception when session is personal")
    void joinWaitlist_WhenPersonalSession_ThrowsBusinessException() {
        com.dev.quikkkk.modules.user.entity.TrainerProfile trainer = com.dev.quikkkk.modules.user.entity.TrainerProfile.builder()
                .id(UUID.randomUUID().toString()).active(true).build();
        TrainingSession session = TrainingSession.builder()
                .id(UUID.randomUUID().toString())
                .status(TrainingStatus.SCHEDULED)
                .type(TrainingType.PERSONAL)
                .maxParticipants(1)
                .clients(new HashSet<>())
                .build();

        when(trainingSessionRepository.findByIdWithPessimisticLock(session.getId())).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> waitlistService.joinWaitlist(session.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", WAITLIST_NOT_ALLOWED_FOR_PERSONAL);
    }

    @Test
    @DisplayName("Should throw exception when session has available slots")
    void joinWaitlist_WhenSlotsAvailable_ThrowsBusinessException() {
        ClientProfile client = createClient();
        com.dev.quikkkk.modules.user.entity.TrainerProfile trainer = com.dev.quikkkk.modules.user.entity.TrainerProfile.builder()
                .id(UUID.randomUUID().toString()).active(true).build();
        TrainingSession session = TrainingSession.builder()
                .id(UUID.randomUUID().toString())
                .status(TrainingStatus.SCHEDULED)
                .type(TrainingType.GROUP)
                .maxParticipants(10)
                .clients(new HashSet<>())
                .build();

        when(trainingSessionRepository.findByIdWithPessimisticLock(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);

        assertThatThrownBy(() -> waitlistService.joinWaitlist(session.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SESSION_HAS_AVAILABLE_SLOTS);
    }

    @Test
    @DisplayName("Should throw exception when client already in waitlist")
    void joinWaitlist_WhenAlreadyInWaitlist_ThrowsBusinessException() {
        ClientProfile client = createClient();
        com.dev.quikkkk.modules.user.entity.TrainerProfile trainer = com.dev.quikkkk.modules.user.entity.TrainerProfile.builder()
                .id(UUID.randomUUID().toString()).active(true).build();

        HashSet<ClientProfile> fullClients = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            fullClients.add(ClientProfile.builder().id(UUID.randomUUID().toString()).build());
        }
        fullClients.add(client);

        TrainingSession session = TrainingSession.builder()
                .id(UUID.randomUUID().toString())
                .status(TrainingStatus.SCHEDULED)
                .type(TrainingType.GROUP)
                .maxParticipants(10)
                .clients(fullClients)
                .build();

        when(trainingSessionRepository.findByIdWithPessimisticLock(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);

        assertThatThrownBy(() -> waitlistService.joinWaitlist(session.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CLIENT_ALREADY_JOINED_SESSION);
    }

    private ClientProfile createClient() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        return ClientProfile.builder().id(UUID.randomUUID().toString()).user(user).build();
    }

    private TrainingSession createFullSession() {
        HashSet<ClientProfile> clients = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            clients.add(ClientProfile.builder().id(UUID.randomUUID().toString()).build());
        }
        return TrainingSession.builder()
                .id(UUID.randomUUID().toString())
                .status(TrainingStatus.SCHEDULED)
                .type(TrainingType.GROUP)
                .maxParticipants(10)
                .clients(clients)
                .build();
    }
}
