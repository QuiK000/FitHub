package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.membership.entity.Membership;
import com.dev.quikkkk.modules.membership.enums.MembershipStatus;
import com.dev.quikkkk.modules.membership.enums.MembershipType;
import com.dev.quikkkk.modules.membership.repository.IMembershipRepository;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
@DisplayName("MembershipRepository Tests")
class MembershipRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IMembershipRepository membershipRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should check if client has active membership")
    void existsByClientIdAndStatus_WithActiveMembership_ReturnsTrue() {
        ClientProfile client = persistClient();
        persistMembership(client, MembershipType.MONTHLY, MembershipStatus.ACTIVE);

        boolean exists = membershipRepository.existsByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when client has no active membership")
    void existsByClientIdAndStatus_WithNoActiveMembership_ReturnsFalse() {
        ClientProfile client = persistClient();

        boolean exists = membershipRepository.existsByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find membership by client id and status")
    void findMembershipByClientIdAndStatus_WithMatchingMembership_ReturnsOptional() {
        ClientProfile client = persistClient();
        Membership membership = persistMembership(client, MembershipType.MONTHLY, MembershipStatus.ACTIVE);

        Optional<Membership> found = membershipRepository.findMembershipByClientIdAndStatus(
                client.getId(), MembershipStatus.ACTIVE);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(membership.getId());
    }

    @Test
    @DisplayName("Should decrement visits successfully")
    void decrementVisits_WithVisitsLeft_DecrementsCount() {
        ClientProfile client = persistClient();
        Membership membership = persistMembership(client, MembershipType.VISITS, MembershipStatus.ACTIVE);
        membership.setVisitsLeft(5);
        entityManager.persist(membership);
        entityManager.flush();

        int remaining = membershipRepository.decrementVisits(membership.getId());

        assertThat(remaining).isEqualTo(4);
    }

    @Test
    @DisplayName("Should return 0 when visits already at zero")
    void decrementVisits_WithZeroVisits_ReturnsZero() {
        ClientProfile client = persistClient();
        Membership membership = persistMembership(client, MembershipType.VISITS, MembershipStatus.ACTIVE);
        membership.setVisitsLeft(0);
        entityManager.persist(membership);
        entityManager.flush();

        int remaining = membershipRepository.decrementVisits(membership.getId());

        assertThat(remaining).isEqualTo(0);
    }

    @Test
    @DisplayName("Should count active memberships")
    void findAllActiveMemberships_WithActiveAndInactive_ReturnsOnlyActiveCount() {
        ClientProfile client = persistClient();
        persistMembership(client, MembershipType.MONTHLY, MembershipStatus.ACTIVE);
        persistMembership(client, MembershipType.MONTHLY, MembershipStatus.EXPIRED);

        Integer count = membershipRepository.findAllActiveMemberships();

        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should find expired memberships by status and end date")
    void findByStatusAndEndDateBefore_WithExpiredMemberships_ReturnsList() {
        ClientProfile client = persistClient();
        Membership expired = persistMembership(client, MembershipType.MONTHLY, MembershipStatus.ACTIVE);
        expired.setEndDate(LocalDateTime.now().minusDays(5));
        entityManager.persist(expired);
        entityManager.flush();

        var result = membershipRepository.findByStatusAndEndDateBefore(
                MembershipStatus.ACTIVE, LocalDateTime.now());

        assertThat(result).isNotEmpty();
    }

    private ClientProfile persistClient() {
        User user = User.builder()
                .email("member-" + UUID.randomUUID() + "@test.com")
                .password("encoded")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(user);

        ClientProfile client = ClientProfile.builder()
                .firstname("Test")
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

    private Membership persistMembership(ClientProfile client, MembershipType type, MembershipStatus status) {
        Membership membership = Membership.builder()
                .type(type)
                .status(status)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .durationMonths(1)
                .client(client)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(membership);
        entityManager.flush();
        return membership;
    }
}
