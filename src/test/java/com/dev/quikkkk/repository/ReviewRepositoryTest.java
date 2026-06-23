package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
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
@DisplayName("ReviewRepository Tests")
class ReviewRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IReviewRepository reviewRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should check if client already reviewed trainer")
    void existsByTrainerIdAndReviewerId_WithExistingReview_ReturnsTrue() {
        TrainerProfile trainer = persistTrainer("trainer-review@test.com");
        ClientProfile client = persistClient("client-review@test.com");
        persistReview(trainer, client, 5, true);

        boolean exists = reviewRepository.existsByTrainerIdAndReviewerId(trainer.getId(), client.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when client has not reviewed trainer")
    void existsByTrainerIdAndReviewerId_WithNoReview_ReturnsFalse() {
        TrainerProfile trainer = persistTrainer("trainer-noreview@test.com");
        ClientProfile client = persistClient("client-noreview@test.com");

        boolean exists = reviewRepository.existsByTrainerIdAndReviewerId(trainer.getId(), client.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find public reviews by trainer id")
    void findAllPublicByTrainerId_WithVisibleAndHiddenReviews_ReturnsOnlyVisible() {
        TrainerProfile trainer = persistTrainer("trainer-public@test.com");
        ClientProfile client1 = persistClient("client1-public@test.com");
        ClientProfile client2 = persistClient("client2-public@test.com");
        persistReview(trainer, client1, 5, true);
        persistReview(trainer, client2, 4, false);

        Page<TrainerReview> result = reviewRepository.findAllPublicByTrainerId(
                trainer.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isVisible()).isTrue();
    }

    @Test
    @DisplayName("Should find reviews by reviewer id")
    void findAllByReviewerId_WithReviews_ReturnsPage() {
        TrainerProfile trainer = persistTrainer("trainer-myreviews@test.com");
        ClientProfile client = persistClient("client-myreviews@test.com");
        persistReview(trainer, client, 5, true);

        Page<TrainerReview> result = reviewRepository.findAllByReviewerId(
                client.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    private User persistUser(String email) {
        User user = User.builder()
                .email(email)
                .password("encoded")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(user);
        return user;
    }

    private TrainerProfile persistTrainer(String email) {
        User user = persistUser(email);
        TrainerProfile trainer = TrainerProfile.builder()
                .firstname("Trainer")
                .lastname("Test")
                .experienceYears(5)
                .active(true)
                .user(user)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(trainer);
        entityManager.flush();
        return trainer;
    }

    private ClientProfile persistClient(String email) {
        User user = persistUser(email);
        ClientProfile client = ClientProfile.builder()
                .firstname("Client")
                .lastname("Test")
                .active(true)
                .user(user)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(client);
        entityManager.flush();
        return client;
    }

    private TrainerReview persistReview(TrainerProfile trainer, ClientProfile client, int rating, boolean visible) {
        TrainerReview review = TrainerReview.builder()
                .trainer(trainer)
                .reviewer(client)
                .rating(rating)
                .visible(visible)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(review);
        entityManager.flush();
        return review;
    }
}
