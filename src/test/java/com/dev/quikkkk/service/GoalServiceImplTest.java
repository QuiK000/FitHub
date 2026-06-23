package com.dev.quikkkk.service;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.modules.progress.dto.request.CreateGoalRequest;
import com.dev.quikkkk.modules.progress.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.modules.progress.dto.response.GoalResponse;
import com.dev.quikkkk.modules.progress.entity.Goal;
import com.dev.quikkkk.modules.progress.enums.GoalStatus;
import com.dev.quikkkk.modules.progress.enums.GoalType;
import com.dev.quikkkk.modules.progress.mapper.GoalMapper;
import com.dev.quikkkk.modules.progress.repository.IGoalRepository;
import com.dev.quikkkk.modules.progress.service.impl.GoalServiceImpl;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.ACTIVE_GOAL_ALREADY_EXISTS;
import static com.dev.quikkkk.core.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.core.enums.ErrorCode.GOAL_ALREADY_COMPLETED;
import static com.dev.quikkkk.core.enums.ErrorCode.GOAL_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoalService Tests")
class GoalServiceImplTest {

    @Mock
    private IGoalRepository goalRepository;
    @Mock
    private ClientProfileUtils clientProfileUtils;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private GoalServiceImpl goalService;

    @Test
    @DisplayName("Should create goal successfully")
    void createGoal_WithValidRequest_ReturnsResponse() {
        ClientProfile client = createClient();
        CreateGoalRequest request = CreateGoalRequest.builder()
                .goalType(GoalType.WEIGHT_LOSS)
                .targetValue(70.0)
                .startValue(80.0)
                .build();
        Goal goal = Goal.builder().id(UUID.randomUUID().toString()).client(client).status(GoalStatus.ACTIVE).build();
        GoalResponse expected = GoalResponse.builder().id(goal.getId()).build();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(goalRepository.existsByClientIdAndGoalTypeAndStatus(client.getId(), GoalType.WEIGHT_LOSS, GoalStatus.ACTIVE))
                .thenReturn(false);
        when(goalMapper.toEntity(request, client)).thenReturn(goal);
        when(goalMapper.toResponse(goal)).thenReturn(expected);

        GoalResponse response = goalService.createGoal(request);

        assertThat(response).isNotNull();
        verify(goalRepository).save(goal);
    }

    @Test
    @DisplayName("Should throw exception when active goal of same type already exists")
    void createGoal_WithExistingActiveGoal_ThrowsBusinessException() {
        ClientProfile client = createClient();
        CreateGoalRequest request = CreateGoalRequest.builder()
                .goalType(GoalType.WEIGHT_LOSS)
                .build();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(goalRepository.existsByClientIdAndGoalTypeAndStatus(client.getId(), GoalType.WEIGHT_LOSS, GoalStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> goalService.createGoal(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ACTIVE_GOAL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Should get goal by id")
    void getGoalById_WithValidId_ReturnsResponse() {
        ClientProfile client = createClient();
        String goalId = UUID.randomUUID().toString();
        Goal goal = Goal.builder().id(goalId).client(client).status(GoalStatus.ACTIVE).build();
        GoalResponse expected = GoalResponse.builder().id(goalId).build();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(goalMapper.toResponse(goal)).thenReturn(expected);

        GoalResponse response = goalService.getGoalById(goalId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(goalId);
    }

    @Test
    @DisplayName("Should throw exception when goal not found")
    void getGoalById_WithNonExistingId_ThrowsBusinessException() {
        ClientProfile client = createClient();
        String goalId = UUID.randomUUID().toString();

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoalById(goalId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GOAL_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw exception when accessing another client's goal")
    void getGoalById_WithForbiddenAccess_ThrowsBusinessException() {
        ClientProfile currentClient = createClient();
        ClientProfile otherClient = createClient();
        String goalId = UUID.randomUUID().toString();
        Goal goal = Goal.builder().id(goalId).client(otherClient).status(GoalStatus.ACTIVE).build();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(currentClient);

        assertThatThrownBy(() -> goalService.getGoalById(goalId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("Should complete goal successfully")
    void completeGoal_WithActiveGoal_CompletesIt() {
        ClientProfile client = createClient();
        String goalId = UUID.randomUUID().toString();
        Goal goal = Goal.builder().id(goalId).client(client).status(GoalStatus.ACTIVE).build();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(messageMapper.message("Goal successfully completed"))
                .thenReturn(MessageResponse.builder().message("Goal successfully completed").build());

        MessageResponse response = goalService.completeGoal(goalId);

        assertThat(response).isNotNull();
        assertThat(goal.getStatus()).isEqualTo(GoalStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should throw exception when completing already completed goal")
    void completeGoal_WhenAlreadyCompleted_ThrowsBusinessException() {
        ClientProfile client = createClient();
        String goalId = UUID.randomUUID().toString();
        Goal goal = Goal.builder().id(goalId).client(client).status(GoalStatus.COMPLETED).build();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);

        assertThatThrownBy(() -> goalService.completeGoal(goalId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", GOAL_ALREADY_COMPLETED);
    }

    @Test
    @DisplayName("Should update goal progress")
    void updateGoalProgress_WithValidRequest_ReturnsResponse() {
        ClientProfile client = createClient();
        String goalId = UUID.randomUUID().toString();
        Goal goal = Goal.builder().id(goalId).client(client).status(GoalStatus.ACTIVE).targetValue(100.0).startValue(0.0).currentValue(50.0).build();

        UpdateGoalProgressRequest request = UpdateGoalProgressRequest.builder().currentValue(75.0).build();
        GoalResponse expected = GoalResponse.builder().id(goalId).build();

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(goalMapper.toResponse(goal)).thenReturn(expected);

        GoalResponse response = goalService.updateGoalProgress(goalId, request);

        assertThat(response).isNotNull();
    }

    private ClientProfile createClient() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        return ClientProfile.builder().id(UUID.randomUUID().toString()).user(user).build();
    }
}
