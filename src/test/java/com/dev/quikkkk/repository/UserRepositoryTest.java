package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
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
@DisplayName("UserRepository Tests")
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find user by email ignoring case")
    void findByEmailIgnoreCase_WithExistingEmail_ReturnsUser() {
        User user = persistUser("test@example.com");

        Optional<User> found = userRepository.findByEmailIgnoreCase("TEST@EXAMPLE.COM");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when email does not exist")
    void findByEmailIgnoreCase_WithNonExistingEmail_ReturnsEmpty() {
        Optional<User> found = userRepository.findByEmailIgnoreCase("nonexistent@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmailIgnoreCase_WithExistingEmail_ReturnsTrue() {
        persistUser("exists@test.com");

        boolean exists = userRepository.existsByEmailIgnoreCase("EXISTS@TEST.COM");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmailIgnoreCase_WithNonExistingEmail_ReturnsFalse() {
        boolean exists = userRepository.existsByEmailIgnoreCase("nope@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should save and retrieve user by id")
    void findById_WithExistingId_ReturnsUser() {
        User user = persistUser("findme@test.com");

        Optional<User> found = userRepository.findById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findme@test.com");
    }

    private User persistUser(String email) {
        User user = User.builder()
                .email(email)
                .password("encoded-password")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
}
