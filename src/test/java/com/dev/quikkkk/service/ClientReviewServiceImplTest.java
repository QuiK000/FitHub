package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.review.dto.request.CreateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateTrainerReviewRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.mapper.ReviewMapper;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.review.service.impl.ClientReviewServiceImpl;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.IClientProfileRepository;
import com.dev.quikkkk.modules.user.repository.ITrainerProfileRepository;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.CANNOT_REVIEW_SELF;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.NO_PRIOR_INTERACTION;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_IS_HIDDEN_AND_CANNOT_BE_EDITED;
import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientReviewService Tests")
class ClientReviewServiceImplTest {

    @Mock
    private IReviewRepository reviewRepository;
    @Mock
    private ITrainerProfileRepository trainerProfileRepository;
    @Mock
    private IClientProfileRepository clientProfileRepository;
    @Mock
    private ITrainingSessionRepository trainingSessionRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private ClientReviewServiceImpl clientReviewService;

    @Test
    @DisplayName("Should create review successfully")
    void createReview_WithValidRequest_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("client-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            User trainerUser = User.builder().id("trainer-user-id").build();
            TrainerProfile trainer = TrainerProfile.builder().id(UUID.randomUUID().toString()).user(trainerUser).build();
            CreateTrainerReviewRequest request = CreateTrainerReviewRequest.builder().rating(5).comment("Great trainer").build();
            TrainerReview review = TrainerReview.builder().id(UUID.randomUUID().toString()).build();
            TrainerReviewResponse expected = TrainerReviewResponse.builder().id(review.getId()).build();

            when(clientProfileRepository.findByUserId("client-user-id")).thenReturn(Optional.of(client));
            when(trainerProfileRepository.findTrainerProfileById(trainer.getId())).thenReturn(Optional.of(trainer));
            when(reviewRepository.existsByTrainerIdAndReviewerId(trainer.getId(), client.getId())).thenReturn(false);
            when(trainingSessionRepository.existsCompletedSessionWithTrainer(trainer.getId(), client.getId())).thenReturn(true);
            when(reviewMapper.toEntity(trainer, client, request)).thenReturn(review);
            when(reviewMapper.toResponse(review)).thenReturn(expected);

            TrainerReviewResponse response = clientReviewService.createReview(request, trainer.getId());

            assertThat(response).isNotNull();
            verify(reviewRepository).save(review);
        }
    }

    @Test
    @DisplayName("Should throw exception when client profile not found")
    void createReview_WhenClientNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("unknown-user-id");

            when(clientProfileRepository.findByUserId("unknown-user-id")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientReviewService.createReview(
                    CreateTrainerReviewRequest.builder().build(), "trainer-id"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CLIENT_PROFILE_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should throw exception when trainer not found")
    void createReview_WhenTrainerNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("client-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            when(clientProfileRepository.findByUserId("client-user-id")).thenReturn(Optional.of(client));
            when(trainerProfileRepository.findTrainerProfileById("unknown-trainer")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientReviewService.createReview(
                    CreateTrainerReviewRequest.builder().build(), "unknown-trainer"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", TRAINER_PROFILE_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should throw exception when reviewing self")
    void createReview_WhenReviewingSelf_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("same-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            User trainerUser = User.builder().id("same-user-id").build();
            TrainerProfile trainer = TrainerProfile.builder().id(UUID.randomUUID().toString()).user(trainerUser).build();

            when(clientProfileRepository.findByUserId("same-user-id")).thenReturn(Optional.of(client));
            when(trainerProfileRepository.findTrainerProfileById(trainer.getId())).thenReturn(Optional.of(trainer));

            assertThatThrownBy(() -> clientReviewService.createReview(
                    CreateTrainerReviewRequest.builder().build(), trainer.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CANNOT_REVIEW_SELF);
        }
    }

    @Test
    @DisplayName("Should throw exception when review already exists")
    void createReview_WhenReviewExists_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("client-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            User trainerUser = User.builder().id("trainer-user-id").build();
            TrainerProfile trainer = TrainerProfile.builder().id(UUID.randomUUID().toString()).user(trainerUser).build();

            when(clientProfileRepository.findByUserId("client-user-id")).thenReturn(Optional.of(client));
            when(trainerProfileRepository.findTrainerProfileById(trainer.getId())).thenReturn(Optional.of(trainer));
            when(reviewRepository.existsByTrainerIdAndReviewerId(trainer.getId(), client.getId())).thenReturn(true);

            assertThatThrownBy(() -> clientReviewService.createReview(
                    CreateTrainerReviewRequest.builder().build(), trainer.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", REVIEW_ALREADY_EXISTS);
        }
    }

    @Test
    @DisplayName("Should throw exception when no completed sessions with trainer")
    void createReview_WhenNoCompletedSessions_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("client-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            User trainerUser = User.builder().id("trainer-user-id").build();
            TrainerProfile trainer = TrainerProfile.builder().id(UUID.randomUUID().toString()).user(trainerUser).build();

            when(clientProfileRepository.findByUserId("client-user-id")).thenReturn(Optional.of(client));
            when(trainerProfileRepository.findTrainerProfileById(trainer.getId())).thenReturn(Optional.of(trainer));
            when(reviewRepository.existsByTrainerIdAndReviewerId(trainer.getId(), client.getId())).thenReturn(false);
            when(trainingSessionRepository.existsCompletedSessionWithTrainer(trainer.getId(), client.getId())).thenReturn(false);

            assertThatThrownBy(() -> clientReviewService.createReview(
                    CreateTrainerReviewRequest.builder().build(), trainer.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NO_PRIOR_INTERACTION);
        }
    }

    @Test
    @DisplayName("Should update review successfully")
    void updateReview_WithValidRequest_ReturnsSuccess() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("client-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            String reviewId = UUID.randomUUID().toString();
            TrainerReview review = TrainerReview.builder().id(reviewId).visible(true).build();
            UpdateTrainerReviewRequest request = UpdateTrainerReviewRequest.builder().rating(4).build();

            when(clientProfileRepository.findByUserId("client-user-id")).thenReturn(Optional.of(client));
            when(reviewRepository.findByIdAndReviewerId(reviewId, client.getId())).thenReturn(Optional.of(review));
            when(messageMapper.message("Review successfully updated"))
                    .thenReturn(MessageResponse.builder().message("Review successfully updated").build());

            MessageResponse response = clientReviewService.updateReview(reviewId, request);

            assertThat(response).isNotNull();
            assertThat(review.isEdited()).isTrue();
        }
    }

    @Test
    @DisplayName("Should throw exception when updating hidden review")
    void updateReview_WhenReviewHidden_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("client-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            String reviewId = UUID.randomUUID().toString();
            TrainerReview review = TrainerReview.builder().id(reviewId).visible(false).build();

            when(clientProfileRepository.findByUserId("client-user-id")).thenReturn(Optional.of(client));
            when(reviewRepository.findByIdAndReviewerId(reviewId, client.getId())).thenReturn(Optional.of(review));

            assertThatThrownBy(() -> clientReviewService.updateReview(reviewId,
                    UpdateTrainerReviewRequest.builder().build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", REVIEW_IS_HIDDEN_AND_CANNOT_BE_EDITED);
        }
    }

    @Test
    @DisplayName("Should throw exception when review not found")
    void updateReview_WhenReviewNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("client-user-id");

            ClientProfile client = ClientProfile.builder().id(UUID.randomUUID().toString()).build();
            String reviewId = UUID.randomUUID().toString();

            when(clientProfileRepository.findByUserId("client-user-id")).thenReturn(Optional.of(client));
            when(reviewRepository.findByIdAndReviewerId(reviewId, client.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientReviewService.updateReview(reviewId,
                    UpdateTrainerReviewRequest.builder().build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", REVIEW_NOT_FOUND);
        }
    }
}
