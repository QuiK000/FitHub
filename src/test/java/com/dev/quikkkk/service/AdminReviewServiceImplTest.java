package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.review.dto.request.AdminReviewFilterRequest;
import com.dev.quikkkk.modules.review.dto.request.UpdateReviewVisibilityRequest;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.entity.TrainerReview;
import com.dev.quikkkk.modules.review.event.ReviewModeratedEvent;
import com.dev.quikkkk.modules.review.mapper.ReviewMapper;
import com.dev.quikkkk.modules.review.repository.IReviewRepository;
import com.dev.quikkkk.modules.review.service.impl.AdminReviewServiceImpl;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.REVIEW_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminReviewService Tests")
class AdminReviewServiceImplTest {

    @Mock
    private IReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AdminReviewServiceImpl adminReviewService;

    @Test
    @DisplayName("Should hide review and publish moderation event")
    void updateReviewVisibility_WhenHiding_PublishesEvent() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String reviewId = UUID.randomUUID().toString();
            User reviewerUser = User.builder().id("reviewer-user-id").build();
            ClientProfile reviewer = ClientProfile.builder().id(UUID.randomUUID().toString()).user(reviewerUser).build();
            TrainerReview review = TrainerReview.builder()
                    .id(reviewId)
                    .visible(true)
                    .reviewer(reviewer)
                    .build();

            UpdateReviewVisibilityRequest request = UpdateReviewVisibilityRequest.builder()
                    .visible(false)
                    .reason("Inappropriate content")
                    .build();
            TrainerReviewResponse expected = TrainerReviewResponse.builder().id(reviewId).build();

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewMapper.toResponse(review)).thenReturn(expected);

            TrainerReviewResponse response = adminReviewService.updateReviewVisibility(reviewId, request);

            assertThat(response).isNotNull();
            assertThat(review.isVisible()).isFalse();
            assertThat(review.getHiddenReason()).isEqualTo("Inappropriate content");
            assertThat(review.getModeratedByAdminId()).isEqualTo("admin-id");
            assertThat(review.getModeratedAt()).isNotNull();

            ArgumentCaptor<ReviewModeratedEvent> eventCaptor = ArgumentCaptor.forClass(ReviewModeratedEvent.class);
            verify(publisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().getClientId()).isEqualTo(reviewer.getId());
        }
    }

    @Test
    @DisplayName("Should show review without publishing event")
    void updateReviewVisibility_WhenShowing_DoesNotPublishEvent() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String reviewId = UUID.randomUUID().toString();
            User reviewerUser = User.builder().id("reviewer-user-id").build();
            ClientProfile reviewer = ClientProfile.builder().id(UUID.randomUUID().toString()).user(reviewerUser).build();
            TrainerReview review = TrainerReview.builder()
                    .id(reviewId)
                    .visible(false)
                    .reviewer(reviewer)
                    .build();

            UpdateReviewVisibilityRequest request = UpdateReviewVisibilityRequest.builder()
                    .visible(true)
                    .build();
            TrainerReviewResponse expected = TrainerReviewResponse.builder().id(reviewId).build();

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewMapper.toResponse(review)).thenReturn(expected);

            TrainerReviewResponse response = adminReviewService.updateReviewVisibility(reviewId, request);

            assertThat(response).isNotNull();
            assertThat(review.isVisible()).isTrue();
            assertThat(review.getHiddenReason()).isNull();
            verify(publisher, never()).publishEvent(any());
        }
    }

    @Test
    @DisplayName("Should throw exception when review not found")
    void updateReviewVisibility_WhenReviewNotFound_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String reviewId = UUID.randomUUID().toString();
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminReviewService.updateReviewVisibility(reviewId,
                    UpdateReviewVisibilityRequest.builder().visible(true).build()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", REVIEW_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should get reviews with filters")
    void getReviews_WithFilters_ReturnsPage() {
        AdminReviewFilterRequest filter = AdminReviewFilterRequest.builder().build();
        TrainerReview review = TrainerReview.builder().id(UUID.randomUUID().toString()).build();
        TrainerReviewResponse expected = TrainerReviewResponse.builder().id(review.getId()).build();
        Page<TrainerReview> page = new PageImpl<>(List.of(review));

        when(reviewRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        when(reviewMapper.toResponse(review)).thenReturn(expected);

        var response = adminReviewService.getReviews(filter, 0, 10);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }
}
