package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.IClientProfileRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
@DisplayName("ClientProfileRepository Tests")
class ClientProfileRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IClientProfileRepository clientProfileRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find client profile by user id")
    void findByUserId_WithExistingUser_ReturnsProfile() {
        ClientProfile client = persistClient("findby-user@test.com");

        Optional<ClientProfile> found = clientProfileRepository.findByUserId(client.getUser().getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(client.getId());
    }

    @Test
    @DisplayName("Should return empty when user id not found")
    void findByUserId_WithNonExistingUser_ReturnsEmpty() {
        Optional<ClientProfile> found = clientProfileRepository.findByUserId(UUID.randomUUID().toString());

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find active clients with optional search")
    void findActiveWithOptionalSearch_WithSearchTerm_ReturnsMatchingProfiles() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistClientWithNames("Active", marker, true);
        persistClientWithNames("Inactive", marker, false);

        Page<ClientProfile> result = clientProfileRepository.findActiveWithOptionalSearch(
                marker, PageRequest.of(0, 10));

        assertThat(result.getContent()).allMatch(ClientProfile::isActive);
    }

    @Test
    @DisplayName("Should count active clients")
    void findAllActiveClients_WithMixedProfiles_ReturnsActiveCount() {
        persistClient("count-active@test.com");
        persistClient("count-inactive@test.com");

        Integer count = clientProfileRepository.findAllActiveClients();

        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    private ClientProfile persistClient(String email) {
        User user = User.builder()
                .email(email)
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

    private ClientProfile persistClientWithNames(String firstname, String lastname, boolean active) {
        User user = User.builder()
                .email(firstname.toLowerCase() + "-" + lastname + "-" + UUID.randomUUID() + "@test.com")
                .password("encoded")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(user);

        ClientProfile client = ClientProfile.builder()
                .firstname(firstname)
                .lastname(lastname)
                .active(active)
                .user(user)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(client);
        entityManager.flush();
        return client;
    }
}
