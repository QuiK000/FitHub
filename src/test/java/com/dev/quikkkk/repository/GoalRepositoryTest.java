package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.progress.entity.Goal;
import com.dev.quikkkk.modules.progress.enums.GoalStatus;
import com.dev.quikkkk.modules.progress.enums.GoalType;
import com.dev.quikkkk.modules.progress.repository.IGoalRepository;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
@DisplayName("GoalRepository Tests")
class GoalRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IGoalRepository goalRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find goals by client id")
    void findAllByClientId_WithGoals_ReturnsPage() {
        ClientProfile client = persistClient();
        persistGoal(client, GoalType.WEIGHT_LOSS, GoalStatus.ACTIVE);
        persistGoal(client, GoalType.STRENGTH, GoalStatus.COMPLETED);

        Page<Goal> result = goalRepository.findAllByClientId(client.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should find goals by client id and status")
    void findAllByClientIdAndStatus_WithStatusFilter_ReturnsFilteredGoals() {
        ClientProfile client = persistClient();
        persistGoal(client, GoalType.WEIGHT_LOSS, GoalStatus.ACTIVE);
        persistGoal(client, GoalType.STRENGTH, GoalStatus.COMPLETED);

        Page<Goal> result = goalRepository.findAllByClientIdAndStatus(
                client.getId(), GoalStatus.ACTIVE, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(GoalStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should check if active goal of type exists")
    void existsByClientIdAndGoalTypeAndStatus_WithActiveGoal_ReturnsTrue() {
        ClientProfile client = persistClient();
        persistGoal(client, GoalType.WEIGHT_LOSS, GoalStatus.ACTIVE);

        boolean exists = goalRepository.existsByClientIdAndGoalTypeAndStatus(
                client.getId(), GoalType.WEIGHT_LOSS, GoalStatus.ACTIVE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when no active goal of type exists")
    void existsByClientIdAndGoalTypeAndStatus_WithNoActiveGoal_ReturnsFalse() {
        ClientProfile client = persistClient();

        boolean exists = goalRepository.existsByClientIdAndGoalTypeAndStatus(
                client.getId(), GoalType.WEIGHT_LOSS, GoalStatus.ACTIVE);

        assertThat(exists).isFalse();
    }

    private ClientProfile persistClient() {
        User user = User.builder()
                .email("goal-user-" + UUID.randomUUID() + "@test.com")
                .password("encoded")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(user);

        ClientProfile client = ClientProfile.builder()
                .firstname("Goal")
                .lastname("Client")
                .active(true)
                .user(user)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(client);
        entityManager.flush();
        return client;
    }

    private Goal persistGoal(ClientProfile client, GoalType type, GoalStatus status) {
        Goal goal = Goal.builder()
                .client(client)
                .goalType(type)
                .status(status)
                .targetValue(70.0)
                .startValue(80.0)
                .currentValue(75.0)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(goal);
        entityManager.flush();
        return goal;
    }
}
