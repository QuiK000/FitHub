package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.nutrition.dto.request.CreateMealPlanRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.MealPlanResponse;
import com.dev.quikkkk.modules.nutrition.entity.MealPlan;
import com.dev.quikkkk.modules.nutrition.mapper.MealPlanMapper;
import com.dev.quikkkk.modules.nutrition.repository.IMealPlanRepository;
import com.dev.quikkkk.modules.nutrition.service.impl.MealPlanServiceImpl;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.DUPLICATE_RESOURCE;
import static com.dev.quikkkk.core.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.core.enums.ErrorCode.MEAL_PLAN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MealPlanService Tests")
class MealPlanServiceImplTest {

    @Mock
    private IMealPlanRepository mealPlanRepository;
    @Mock
    private MealPlanMapper mealPlanMapper;
    @Mock
    private ClientProfileUtils clientProfileUtils;

    @InjectMocks
    private MealPlanServiceImpl mealPlanService;

    @Test
    @DisplayName("Should create meal plan successfully")
    void createMealPlan_WithValidRequest_ReturnsResponse() {
        ClientProfile client = createClient();
        LocalDate planDate = LocalDate.now();
        CreateMealPlanRequest request = CreateMealPlanRequest.builder()
                .planDate(planDate)
                .targetCalories(2000)
                .build();
        MealPlan mealPlan = MealPlan.builder().id(UUID.randomUUID().toString()).client(client).planDate(planDate).build();
        MealPlanResponse expected = MealPlanResponse.builder().id(mealPlan.getId()).build();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(mealPlanRepository.findByClientIdAndPlanDate(client.getId(), planDate)).thenReturn(Optional.empty());
        when(mealPlanMapper.toEntity(request, client)).thenReturn(mealPlan);
        when(mealPlanMapper.toResponse(mealPlan)).thenReturn(expected);

        MealPlanResponse response = mealPlanService.createMealPlan(request);

        assertThat(response).isNotNull();
        verify(mealPlanRepository).save(mealPlan);
    }

    @Test
    @DisplayName("Should throw exception when meal plan for date already exists")
    void createMealPlan_WhenDuplicateDate_ThrowsBusinessException() {
        ClientProfile client = createClient();
        LocalDate planDate = LocalDate.now();
        CreateMealPlanRequest request = CreateMealPlanRequest.builder().planDate(planDate).build();
        MealPlan existingPlan = MealPlan.builder().id(UUID.randomUUID().toString()).build();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(mealPlanRepository.findByClientIdAndPlanDate(client.getId(), planDate)).thenReturn(Optional.of(existingPlan));

        assertThatThrownBy(() -> mealPlanService.createMealPlan(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_RESOURCE);
    }

    @Test
    @DisplayName("Should get meal plan by id")
    void getMealPlanById_WithValidId_ReturnsResponse() {
        ClientProfile client = createClient();
        String planId = UUID.randomUUID().toString();
        MealPlan mealPlan = MealPlan.builder().id(planId).client(client).build();
        MealPlanResponse expected = MealPlanResponse.builder().id(planId).build();

        when(mealPlanRepository.findById(planId)).thenReturn(Optional.of(mealPlan));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(mealPlanMapper.toResponse(mealPlan)).thenReturn(expected);

        MealPlanResponse response = mealPlanService.getMealPlanById(planId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(planId);
    }

    @Test
    @DisplayName("Should throw exception when meal plan not found")
    void getMealPlanById_WithNonExistingId_ThrowsBusinessException() {
        ClientProfile client = createClient();
        String planId = UUID.randomUUID().toString();

        when(mealPlanRepository.findById(planId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mealPlanService.getMealPlanById(planId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEAL_PLAN_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw exception when accessing another client's meal plan")
    void getMealPlanById_WithForbiddenAccess_ThrowsBusinessException() {
        ClientProfile currentClient = createClient();
        ClientProfile otherClient = createClient();
        String planId = UUID.randomUUID().toString();
        MealPlan mealPlan = MealPlan.builder().id(planId).client(otherClient).build();

        when(mealPlanRepository.findById(planId)).thenReturn(Optional.of(mealPlan));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(currentClient);

        assertThatThrownBy(() -> mealPlanService.getMealPlanById(planId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", FORBIDDEN_ACCESS);
    }

    private ClientProfile createClient() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        return ClientProfile.builder().id(UUID.randomUUID().toString()).user(user).build();
    }
}
