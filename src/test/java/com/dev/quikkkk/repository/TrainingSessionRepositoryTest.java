package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.Specialization;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
import com.dev.quikkkk.modules.workout.enums.TrainingStatus;
import com.dev.quikkkk.modules.workout.enums.TrainingType;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
class TrainingSessionRepositoryTest extends AbstractIntegrationTest {

    private static final String CREATED_BY = "repository-test";

    @Autowired
    private ITrainingSessionRepository trainingSessionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findAllWithOptionalSearch_searchMatchesSpecializationIgnoringCase_returnsDistinctSession() {
        // given
        String marker = uniqueMarker();
        Specialization specialization = persistSpecialization("Mobility-" + marker);
        TrainerProfile trainer = persistTrainer("Alice-" + marker, "Coach", specialization);
        TrainingSession session = persistSession(
                trainer,
                TrainingType.GROUP,
                TrainingStatus.SCHEDULED,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1)
        );

        // when
        Page<TrainingSession> result = trainingSessionRepository.findAllWithOptionalSearch(
                "mobility-" + marker.toLowerCase(),
                PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getContent())
                .extracting(TrainingSession::getId)
                .containsExactly(session.getId());
    }

    @Test
    void findAllWithOptionalSearch_blankSearch_returnsSessionsWithoutFiltering() {
        // given
        TrainerProfile trainer = persistTrainer("Blank", "Search");
        TrainingSession session = persistSession(
                trainer,
                TrainingType.PERSONAL,
                TrainingStatus.SCHEDULED,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1)
        );

        // when
        Page<TrainingSession> result = trainingSessionRepository.findAllWithOptionalSearch(
                "   ",
                PageRequest.of(0, 100)
        );

        // then
        assertThat(result.getContent())
                .extracting(TrainingSession::getId)
                .contains(session.getId());
    }

    @Test
    void existsClientInSession_clientIsBooked_returnsTrue() {
        // given
        TrainerProfile trainer = persistTrainer("Booked", "Trainer");
        ClientProfile client = persistClient();
        TrainingSession session = persistSession(
                trainer,
                TrainingType.GROUP,
                TrainingStatus.SCHEDULED,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                client
        );

        // when
        boolean exists = trainingSessionRepository.existsClientInSession(session.getId(), client.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void countAllSessionsByTrainer_multipleTrainerSessions_returnsTrainerSpecificCount() {
        // given
        TrainerProfile trainer = persistTrainer("Counting", "Trainer");
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.SCHEDULED, daysFromNow(1), daysFromNow(1).plusHours(1));
        persistSession(trainer, TrainingType.PERSONAL, TrainingStatus.CANCELLED, daysFromNow(2), daysFromNow(2).plusHours(1));

        // when
        long result = trainingSessionRepository.countAllSessionsByTrainer(trainer.getId());

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    void countPlannedSessionsByClient_pastAndFutureSessions_returnsPastSessionCount() {
        // given
        TrainerProfile trainer = persistTrainer("Historical", "Trainer");
        ClientProfile client = persistClient();
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.COMPLETED, daysFromNow(-2), daysFromNow(-2).plusHours(1), client);
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.SCHEDULED, daysFromNow(2), daysFromNow(2).plusHours(1), client);

        // when
        long result = trainingSessionRepository.countPlannedSessionsByClient(client.getId());

        // then
        // The current JPQL counts historical sessions despite the repository method name.
        assertThat(result).isEqualTo(1);
    }

    @Test
    void hasOverlappingSession_requestTouchesScheduledSessionBoundary_returnsTrue() {
        // given
        TrainerProfile trainer = persistTrainer("Overlap", "Trainer");
        LocalDateTime existingStart = daysFromNow(3);
        LocalDateTime existingEnd = existingStart.plusHours(1);
        persistSession(trainer, TrainingType.PERSONAL, TrainingStatus.SCHEDULED, existingStart, existingEnd);

        // when
        boolean overlaps = trainingSessionRepository.hasOverlappingSession(
                trainer.getId(),
                existingEnd,
                existingEnd.plusHours(1)
        );

        // then
        assertThat(overlaps).isTrue();
    }

    @Test
    void hasOverlappingSession_overlappingCancelledSession_returnsFalse() {
        // given
        TrainerProfile trainer = persistTrainer("Cancelled", "Trainer");
        LocalDateTime existingStart = daysFromNow(3);
        LocalDateTime existingEnd = existingStart.plusHours(1);
        persistSession(trainer, TrainingType.PERSONAL, TrainingStatus.CANCELLED, existingStart, existingEnd);

        // when
        boolean overlaps = trainingSessionRepository.hasOverlappingSession(
                trainer.getId(),
                existingStart.plusMinutes(15),
                existingEnd.minusMinutes(15)
        );

        // then
        assertThat(overlaps).isFalse();
    }

    @Test
    void findByIdWithPessimisticLock_existingSession_returnsSession() {
        // given
        TrainerProfile trainer = persistTrainer("Locked", "Trainer");
        TrainingSession session = persistSession(
                trainer,
                TrainingType.GROUP,
                TrainingStatus.SCHEDULED,
                daysFromNow(1),
                daysFromNow(1).plusHours(1)
        );

        // when
        TrainingSession result = trainingSessionRepository.findByIdWithPessimisticLock(session.getId()).orElseThrow();

        // then
        assertThat(result.getId()).isEqualTo(session.getId());
    }

    @Test
    void findAllByTrainerIdAndStatusAndStartTimeAfter_matchingUpcomingSession_returnsOnlyMatchingSessions() {
        // given
        TrainerProfile trainer = persistTrainer("Upcoming", "Trainer");
        LocalDateTime now = LocalDateTime.now();
        TrainingSession expected = persistSession(trainer, TrainingType.GROUP, TrainingStatus.SCHEDULED, now.plusDays(1), now.plusDays(1).plusHours(1));
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.COMPLETED, now.plusDays(2), now.plusDays(2).plusHours(1));
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.SCHEDULED, now.minusDays(1), now.minusDays(1).plusHours(1));

        // when
        List<TrainingSession> result = trainingSessionRepository.findAllByTrainerIdAndStatusAndStartTimeAfter(
                trainer.getId(),
                TrainingStatus.SCHEDULED,
                now
        );

        // then
        assertThat(result)
                .extracting(TrainingSession::getId)
                .containsExactly(expected.getId());
    }

    @Test
    void countFutureBookings_scheduledFutureAndOtherSessions_returnsOnlyScheduledFutureCount() {
        // given
        TrainerProfile trainer = persistTrainer("Future", "Trainer");
        ClientProfile client = persistClient();
        LocalDateTime now = LocalDateTime.now();
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.SCHEDULED, now.plusDays(1), now.plusDays(1).plusHours(1), client);
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.CANCELLED, now.plusDays(2), now.plusDays(2).plusHours(1), client);
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.SCHEDULED, now.minusDays(2), now.minusDays(2).plusHours(1), client);

        // when
        long result = trainingSessionRepository.countFutureBookings(client.getId(), now);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void existsCompletedSessionWithTrainer_completedClientSession_returnsTrue() {
        // given
        TrainerProfile trainer = persistTrainer("Completed", "Trainer");
        ClientProfile client = persistClient();
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.COMPLETED, daysFromNow(-3), daysFromNow(-3).plusHours(1), client);

        // when
        boolean exists = trainingSessionRepository.existsCompletedSessionWithTrainer(trainer.getId(), client.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsCompletedSessionWithTrainer_onlyScheduledClientSession_returnsFalse() {
        // given
        TrainerProfile trainer = persistTrainer("Scheduled", "Trainer");
        ClientProfile client = persistClient();
        persistSession(trainer, TrainingType.GROUP, TrainingStatus.SCHEDULED, daysFromNow(3), daysFromNow(3).plusHours(1), client);

        // when
        boolean exists = trainingSessionRepository.existsCompletedSessionWithTrainer(trainer.getId(), client.getId());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void insertTrainingSession_nullTrainingType_violatesDatabaseConstraint() {
        // given
        TrainerProfile trainer = persistTrainer("Constraint", "Trainer");

        // when
        // then
        assertThatThrownBy(() -> jdbcTemplate.update("""
                INSERT INTO training_sessions (
                    id, training_type, start_time, end_time, max_participants,
                    training_status, trainer_id, created_date, created_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID().toString(),
                null,
                daysFromNow(1),
                daysFromNow(1).plusHours(1),
                10,
                TrainingStatus.SCHEDULED.name(),
                trainer.getId(),
                LocalDateTime.now(),
                CREATED_BY
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void insertTrainingSession_unknownTrainer_violatesForeignKeyConstraint() {
        // given
        String unknownTrainerId = UUID.randomUUID().toString();

        // when
        // then
        assertThatThrownBy(() -> jdbcTemplate.update("""
                INSERT INTO training_sessions (
                    id, training_type, start_time, end_time, max_participants,
                    training_status, trainer_id, created_date, created_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID().toString(),
                TrainingType.GROUP.name(),
                daysFromNow(1),
                daysFromNow(1).plusHours(1),
                10,
                TrainingStatus.SCHEDULED.name(),
                unknownTrainerId,
                LocalDateTime.now(),
                CREATED_BY
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    private TrainerProfile persistTrainer(String firstname, String lastname, Specialization... specializations) {
        User user = persistUser("trainer");
        TrainerProfile trainer = TrainerProfile.builder()
                .firstname(firstname)
                .lastname(lastname)
                .experienceYears(5)
                .description("Repository test trainer")
                .active(true)
                .user(user)
                .specialization(new HashSet<>(List.of(specializations)))
                .createdDate(LocalDateTime.now())
                .createdBy(CREATED_BY)
                .build();
        entityManager.persist(trainer);
        return trainer;
    }

    private ClientProfile persistClient() {
        User user = persistUser("client");
        ClientProfile client = ClientProfile.builder()
                .firstname("Repository")
                .lastname("Client")
                .phone("+380" + UUID.randomUUID().toString().replace("-", "").substring(0, 9))
                .active(true)
                .user(user)
                .createdDate(LocalDateTime.now())
                .createdBy(CREATED_BY)
                .build();
        entityManager.persist(client);
        return client;
    }

    private User persistUser(String prefix) {
        User user = User.builder()
                .email(prefix + "-" + UUID.randomUUID() + "@test.local")
                .password("encoded-password")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy(CREATED_BY)
                .build();
        entityManager.persist(user);
        return user;
    }

    private Specialization persistSpecialization(String name) {
        Specialization specialization = Specialization.builder()
                .name(name)
                .description("Repository test specialization")
                .active(true)
                .build();
        entityManager.persist(specialization);
        return specialization;
    }

    private TrainingSession persistSession(
            TrainerProfile trainer,
            TrainingType type,
            TrainingStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            ClientProfile... clients
    ) {
        TrainingSession session = TrainingSession.builder()
                .trainer(trainer)
                .type(type)
                .status(status)
                .startTime(startTime)
                .endTime(endTime)
                .maxParticipants(10)
                .clients(new HashSet<>(List.of(clients)))
                .createdDate(LocalDateTime.now())
                .createdBy(CREATED_BY)
                .build();
        entityManager.persist(session);
        entityManager.flush();
        return session;
    }

    private LocalDateTime daysFromNow(int days) {
        return LocalDateTime.now().plusDays(days);
    }

    private String uniqueMarker() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
