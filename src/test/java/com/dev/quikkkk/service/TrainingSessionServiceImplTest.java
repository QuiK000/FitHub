package com.dev.quikkkk.service;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.functional.LockOperation;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.security.UserPrincipal;
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
import com.dev.quikkkk.modules.workout.service.impl.TrainingSessionServiceImpl;
import com.dev.quikkkk.modules.workout.validator.TrainingSessionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.NO_ACTIVE_MEMBERSHIP;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_CLOSED;
import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.START_TIME_IN_PAST;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_SESSION_OVERLAP;
import static com.dev.quikkkk.core.enums.ErrorCode.VISITS_LIMIT_REACHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceImplTest {

    private static final String CURRENT_USER_ID = "trainer-user-id";

    @Mock
    private ITrainingSessionRepository trainingSessionRepository;
    @Mock
    private ITrainerProfileRepository trainerProfileRepository;
    @Mock
    private IClientProfileRepository clientProfileRepository;
    @Mock
    private IAttendanceRepository attendanceRepository;
    @Mock
    private IMembershipRepository membershipRepository;
    @Mock
    private TrainingSessionMapper trainingSessionMapper;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private ISessionLockService sessionLockService;
    @Mock
    private ClientProfileUtils clientProfileUtils;
    @Mock
    private TrainingSessionValidator sessionValidator;

    @InjectMocks
    private TrainingSessionServiceImpl trainingSessionService;

    @BeforeEach
    void setUpSecurityContext() {
        // given
        UserPrincipal principal = new UserPrincipal(CURRENT_USER_ID, "trainer@test.local", Set.of("TRAINER"));

        // when
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of())
        );

        // then
        assertThat(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal()).isEqualTo(principal);
    }

    @AfterEach
    void clearSecurityContext() {
        // given
        SecurityContextHolder.clearContext();

        // when
        Object authentication = SecurityContextHolder.getContext().getAuthentication();

        // then
        assertThat(authentication).isNull();
    }

    @Test
    void createSession_validRequest_savesAndReturnsMappedSession() {
        // given
        TrainerProfile trainer = trainer();
        CreateTrainingSessionRequest request = createRequest(daysFromNow(1));
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        TrainingSessionResponse expected = response(session);

        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionMapper.toEntity(request, trainer)).thenReturn(session);
        when(trainingSessionMapper.toResponse(session)).thenReturn(expected);

        // when
        TrainingSessionResponse result = trainingSessionService.createSession(request);

        // then
        assertThat(result).isSameAs(expected);
        verify(sessionValidator).validateTrainerIsActive(trainer);
        verify(sessionValidator).validateSessionCreationRequest(request);
        verify(sessionValidator).validateTrainerSessionOverlap(trainer, request);
        verify(trainingSessionRepository, times(1)).save(session);
    }

    @Test
    void createSession_startTimeInPast_propagatesBusinessExceptionAndDoesNotSave() {
        // given
        TrainerProfile trainer = trainer();
        CreateTrainingSessionRequest request = createRequest(daysFromNow(-1));
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        doThrow(new BusinessException(START_TIME_IN_PAST))
                .when(sessionValidator).validateSessionCreationRequest(request);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.createSession(request)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(START_TIME_IN_PAST);
        verify(trainingSessionRepository, never()).save(any());
        verifyNoInteractions(trainingSessionMapper);
    }

    @Test
    void createSession_overlappingTrainerSession_propagatesBusinessExceptionAndDoesNotSave() {
        // given
        TrainerProfile trainer = trainer();
        CreateTrainingSessionRequest request = createRequest(daysFromNow(1));
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        doThrow(new BusinessException(TRAINER_SESSION_OVERLAP))
                .when(sessionValidator).validateTrainerSessionOverlap(trainer, request);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.createSession(request)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(TRAINER_SESSION_OVERLAP);
        verify(trainingSessionRepository, never()).save(any());
        verifyNoInteractions(trainingSessionMapper);
    }

    @Test
    void createSession_missingTrainerProfile_throwsBusinessException() {
        // given
        CreateTrainingSessionRequest request = createRequest(daysFromNow(1));
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.createSession(request)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(TRAINER_PROFILE_NOT_FOUND);
        verifyNoInteractions(sessionValidator, trainingSessionMapper, trainingSessionRepository);
    }

    @Test
    void getTrainingSessions_existingSessions_returnsMappedPage() {
        // given
        TrainerProfile trainer = trainer();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        TrainingSessionResponse expected = response(session);
        Page<TrainingSession> sessionPage = new PageImpl<>(List.of(session));
        when(trainingSessionRepository.findAllWithOptionalSearch(eq("group"), any(Pageable.class)))
                .thenReturn(sessionPage);
        when(trainingSessionMapper.toResponse(session)).thenReturn(expected);

        // when
        PageResponse<TrainingSessionResponse> result = trainingSessionService.getTrainingSessions(0, 20, "group");

        // then
        assertThat(result.getContent()).containsExactly(expected);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(trainingSessionRepository, times(1))
                .findAllWithOptionalSearch(eq("group"), any(Pageable.class));
        verify(trainingSessionMapper, times(1)).toResponse(session);
    }

    @Test
    void updateSession_validRequest_savesAndReturnsMappedSession() {
        // given
        TrainerProfile trainer = trainer();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        UpdateTrainingSessionRequest request = UpdateTrainingSessionRequest.builder()
                .maxParticipants(12)
                .build();
        TrainingSessionResponse expected = response(session);
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(trainingSessionMapper.toResponse(session)).thenReturn(expected);

        // when
        TrainingSessionResponse result = trainingSessionService.updateSession(session.getId(), request);

        // then
        assertThat(result).isSameAs(expected);
        verify(sessionValidator).validateTrainerOwnership(session, trainer);
        verify(sessionValidator).validateSessionIsEditable(session);
        verify(sessionValidator).validatePersonalTrainingParticipants(session, request);
        verify(trainingSessionMapper).updateSession(session, request);
        verify(trainingSessionRepository, times(1)).save(session);
    }

    @Test
    void updateSession_closedSession_propagatesBusinessExceptionAndDoesNotSave() {
        // given
        TrainerProfile trainer = trainer();
        TrainingSession session = session(trainer, TrainingStatus.COMPLETED);
        UpdateTrainingSessionRequest request = UpdateTrainingSessionRequest.builder()
                .maxParticipants(12)
                .build();
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        doThrow(new BusinessException(SESSION_CLOSED)).when(sessionValidator).validateSessionIsEditable(session);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.updateSession(session.getId(), request)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SESSION_CLOSED);
        verify(trainingSessionRepository, never()).save(any());
        verify(trainingSessionMapper, never()).updateSession(any(), any());
    }

    @Test
    void updateSession_missingSession_throwsBusinessException() {
        // given
        TrainerProfile trainer = trainer();
        String sessionId = UUID.randomUUID().toString();
        UpdateTrainingSessionRequest request = UpdateTrainingSessionRequest.builder().build();
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.updateSession(sessionId, request)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SESSION_NOT_FOUND);
        verify(trainingSessionRepository, never()).save(any());
        verifyNoInteractions(sessionValidator, trainingSessionMapper);
    }

    @Test
    void joinToSession_validClientAndMembership_addsClientAndSavesInsideLock() {
        // given
        TrainerProfile trainer = trainer();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        ClientProfile client = client();
        Membership membership = membership(client, MembershipType.VISITS, 5);
        MessageResponse expected = MessageResponse.builder().message("Successfully joined training session").build();
        executeLockOperationImmediately();
        when(trainingSessionRepository.findByIdWithPessimisticLock(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE))
                .thenReturn(Optional.of(membership));
        when(messageMapper.message("Successfully joined training session")).thenReturn(expected);

        // when
        MessageResponse result = trainingSessionService.joinToSession(session.getId());

        // then
        assertThat(result).isSameAs(expected);
        assertThat(session.getClients()).containsExactly(client);
        verify(sessionLockService, times(1)).executeWithLock(eq(session.getId()), any());
        verify(sessionValidator).validateSessionJoinAvailability(session);
        verify(sessionValidator).ensureClientNotAlreadyJoined(session, client);
        verify(sessionValidator).validateMembershipStatus(membership);
        verify(sessionValidator).validateMembershipForSession(membership, session);
        verify(trainingSessionRepository, times(1)).save(session);
    }

    @Test
    void joinToSession_missingSession_throwsBusinessExceptionInsideLock() {
        // given
        String sessionId = UUID.randomUUID().toString();
        executeLockOperationImmediately();
        when(trainingSessionRepository.findByIdWithPessimisticLock(sessionId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.joinToSession(sessionId)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SESSION_NOT_FOUND);
        verify(trainingSessionRepository, never()).save(any());
        verifyNoInteractions(clientProfileUtils, membershipRepository, messageMapper);
    }

    @Test
    void joinToSession_clientWithoutActiveMembership_throwsBusinessExceptionAndDoesNotSave() {
        // given
        TrainerProfile trainer = trainer();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        ClientProfile client = client();
        executeLockOperationImmediately();
        when(trainingSessionRepository.findByIdWithPessimisticLock(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.joinToSession(session.getId())
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(NO_ACTIVE_MEMBERSHIP);
        verify(trainingSessionRepository, never()).save(any());
        verify(messageMapper, never()).message(any());
    }

    @Test
    void checkIn_visitsMembership_decrementsVisitAndSavesAttendance() {
        // given
        TrainerProfile trainer = trainer();
        ClientProfile client = client();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        Membership membership = membership(client, MembershipType.VISITS, 3);
        CheckInTrainingSessionRequest request = CheckInTrainingSessionRequest.builder()
                .clientId(client.getId())
                .build();
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(membershipRepository.findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE))
                .thenReturn(Optional.of(membership));
        when(membershipRepository.decrementVisits(membership.getId())).thenReturn(1);
        ArgumentCaptor<Attendance> attendanceCaptor = ArgumentCaptor.forClass(Attendance.class);

        // when
        CheckInResponse result = trainingSessionService.checkIn(session.getId(), request);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getSessionId()).isEqualTo(session.getId());
        assertThat(result.getClientId()).isEqualTo(client.getId());
        assertThat(result.getMessage()).isEqualTo("Client successfully checked in. Visits left: 2");
        assertThat(membership.getVisitsLeft()).isEqualTo(2);
        verify(sessionValidator).validateTrainerOwnership(session, trainer);
        verify(sessionValidator).validateSessionForCheckIn(eq(session), any(LocalDateTime.class));
        verify(sessionValidator).ensureClientJoinedSession(session, client);
        verify(sessionValidator).ensureClientNotCheckedIn(client, session.getId());
        verify(sessionValidator).validateMembershipStatus(membership);
        verify(membershipRepository, times(1)).decrementVisits(membership.getId());
        verify(attendanceRepository, times(1)).save(attendanceCaptor.capture());
        assertThat(attendanceCaptor.getValue())
                .extracting(Attendance::getClient, Attendance::getSession, Attendance::getCreatedBy)
                .containsExactly(client, session, trainer.getId());
    }

    @Test
    void checkIn_unlimitedMembership_validatesExpirationAndSavesAttendance() {
        // given
        TrainerProfile trainer = trainer();
        ClientProfile client = client();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        Membership membership = membership(client, MembershipType.MONTHLY, null);
        CheckInTrainingSessionRequest request = CheckInTrainingSessionRequest.builder()
                .clientId(client.getId())
                .build();
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(membershipRepository.findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE))
                .thenReturn(Optional.of(membership));

        // when
        CheckInResponse result = trainingSessionService.checkIn(session.getId(), request);

        // then
        assertThat(result.getMessage()).isEqualTo("Client successfully checked in. Visits left: unlimited");
        verify(sessionValidator).validateMembershipExpiration(eq(membership), any(LocalDateTime.class));
        verify(membershipRepository, never()).decrementVisits(any());
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void checkIn_visitsMembershipCannotBeDecremented_throwsBusinessExceptionAndDoesNotSaveAttendance() {
        // given
        TrainerProfile trainer = trainer();
        ClientProfile client = client();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        Membership membership = membership(client, MembershipType.VISITS, 1);
        CheckInTrainingSessionRequest request = CheckInTrainingSessionRequest.builder()
                .clientId(client.getId())
                .build();
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(membershipRepository.findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE))
                .thenReturn(Optional.of(membership));
        when(membershipRepository.decrementVisits(membership.getId())).thenReturn(0);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.checkIn(session.getId(), request)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(VISITS_LIMIT_REACHED);
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void checkIn_missingClient_throwsBusinessException() {
        // given
        TrainerProfile trainer = trainer();
        TrainingSession session = session(trainer, TrainingStatus.SCHEDULED);
        String clientId = UUID.randomUUID().toString();
        CheckInTrainingSessionRequest request = CheckInTrainingSessionRequest.builder()
                .clientId(clientId)
                .build();
        when(trainerProfileRepository.findTrainerProfileByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(trainer));
        when(trainingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(clientProfileRepository.findById(clientId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> trainingSessionService.checkIn(session.getId(), request)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(CLIENT_PROFILE_NOT_FOUND);
        verify(attendanceRepository, never()).save(any());
        verifyNoInteractions(membershipRepository);
    }

    private void executeLockOperationImmediately() {
        doAnswer(invocation -> invocation.<LockOperation<?>>getArgument(1).execute())
                .when(sessionLockService)
                .executeWithLock(any(), any());
    }

    private TrainerProfile trainer() {
        return TrainerProfile.builder()
                .id(UUID.randomUUID().toString())
                .firstname("Jane")
                .lastname("Coach")
                .active(true)
                .build();
    }

    private ClientProfile client() {
        return ClientProfile.builder()
                .id(UUID.randomUUID().toString())
                .firstname("John")
                .lastname("Client")
                .active(true)
                .build();
    }

    private Membership membership(ClientProfile client, MembershipType type, Integer visitsLeft) {
        return Membership.builder()
                .id(UUID.randomUUID().toString())
                .client(client)
                .type(type)
                .status(MembershipStatus.ACTIVE)
                .visitsLeft(visitsLeft)
                .endDate(daysFromNow(30))
                .build();
    }

    private TrainingSession session(TrainerProfile trainer, TrainingStatus status) {
        return TrainingSession.builder()
                .id(UUID.randomUUID().toString())
                .trainer(trainer)
                .type(TrainingType.GROUP)
                .status(status)
                .startTime(daysFromNow(1))
                .endTime(daysFromNow(1).plusHours(1))
                .maxParticipants(10)
                .clients(new HashSet<>())
                .build();
    }

    private CreateTrainingSessionRequest createRequest(LocalDateTime startTime) {
        return CreateTrainingSessionRequest.builder()
                .type(TrainingType.GROUP)
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .maxParticipants(10)
                .build();
    }

    private TrainingSessionResponse response(TrainingSession session) {
        return TrainingSessionResponse.builder()
                .id(session.getId())
                .type(session.getType())
                .status(session.getStatus())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .maxParticipants(session.getMaxParticipants())
                .build();
    }

    private LocalDateTime daysFromNow(int days) {
        return LocalDateTime.now().plusDays(days);
    }
}
