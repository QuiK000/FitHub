package com.dev.quikkkk.service;

import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.mapper.MessageMapper;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.nutrition.dto.request.CreateFoodRequest;
import com.dev.quikkkk.modules.nutrition.dto.response.FoodResponse;
import com.dev.quikkkk.modules.nutrition.entity.Food;
import com.dev.quikkkk.modules.nutrition.mapper.FoodMapper;
import com.dev.quikkkk.modules.nutrition.repository.IFoodRepository;
import com.dev.quikkkk.modules.nutrition.service.impl.FoodServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.DUPLICATE_RESOURCE;
import static com.dev.quikkkk.core.enums.ErrorCode.FOOD_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FoodService Tests")
class FoodServiceImplTest {

    @Mock
    private IFoodRepository foodRepository;
    @Mock
    private FoodMapper foodMapper;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private FoodServiceImpl foodService;

    @Test
    @DisplayName("Should create food successfully")
    void createFood_WithValidRequest_ReturnsResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            CreateFoodRequest request = CreateFoodRequest.builder()
                    .name("Chicken Breast")
                    .brand("Tyson")
                    .barcode("123456")
                    .build();
            Food food = new Food();
            food.setId(UUID.randomUUID().toString());
            food.setName("Chicken Breast");
            FoodResponse expected = FoodResponse.builder().id(food.getId()).name("Chicken Breast").build();

            when(foodRepository.existsByBarcodeAndActiveIsTrue("123456")).thenReturn(false);
            when(foodRepository.existsByNameAndBrandAndActiveIsTrue("Chicken Breast", "Tyson")).thenReturn(false);
            when(foodMapper.toEntity(request, "user-id")).thenReturn(food);
            when(foodMapper.toResponse(food)).thenReturn(expected);

            FoodResponse response = foodService.createFood(request);

            assertThat(response).isNotNull();
            verify(foodRepository).save(food);
        }
    }

    @Test
    @DisplayName("Should throw exception when food barcode already exists")
    void createFood_WithExistingBarcode_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            CreateFoodRequest request = CreateFoodRequest.builder()
                    .name("Apple")
                    .barcode("111111")
                    .build();

            when(foodRepository.existsByBarcodeAndActiveIsTrue("111111")).thenReturn(true);

            assertThatThrownBy(() -> foodService.createFood(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_RESOURCE);
        }
    }

    @Test
    @DisplayName("Should throw exception when food name+brand already exists")
    void createFood_WithDuplicateNameAndBrand_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("user-id");

            CreateFoodRequest request = CreateFoodRequest.builder()
                    .name("Banana")
                    .brand("Chiquita")
                    .build();

            when(foodRepository.existsByNameAndBrandAndActiveIsTrue("Banana", "Chiquita")).thenReturn(true);

            assertThatThrownBy(() -> foodService.createFood(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", DUPLICATE_RESOURCE);
        }
    }

    @Test
    @DisplayName("Should get food by id")
    void getFoodById_WithValidId_ReturnsResponse() {
        String foodId = UUID.randomUUID().toString();
        Food food = new Food();
        food.setId(foodId);
        food.setName("Rice");
        FoodResponse expected = FoodResponse.builder().id(foodId).name("Rice").build();

        when(foodRepository.findFoodByIdAndActiveIsTrue(foodId)).thenReturn(Optional.of(food));
        when(foodMapper.toResponse(food)).thenReturn(expected);

        FoodResponse response = foodService.getFoodById(foodId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(foodId);
    }

    @Test
    @DisplayName("Should throw exception when food not found")
    void getFoodById_WithNonExistingId_ThrowsBusinessException() {
        String foodId = UUID.randomUUID().toString();
        when(foodRepository.findFoodByIdAndActiveIsTrue(foodId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.getFoodById(foodId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", FOOD_NOT_FOUND);
    }

    @Test
    @DisplayName("Should deactivate food")
    void deactivateFood_WithActiveFood_DeactivatesIt() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn("admin-id");

            String foodId = UUID.randomUUID().toString();
            Food food = new Food();
            food.setId(foodId);
            food.setActive(true);

            when(foodRepository.findFoodByIdAndActiveIsTrue(foodId)).thenReturn(Optional.of(food));
            when(messageMapper.message("Food has been deactivated"))
                    .thenReturn(MessageResponse.builder().message("Food has been deactivated").build());

            MessageResponse response = foodService.deactivateFood(foodId);

            assertThat(response).isNotNull();
            assertThat(food.isActive()).isFalse();
            verify(foodRepository).save(food);
        }
    }

    @Test
    @DisplayName("Should get food by barcode")
    void getFoodByBarcode_WithExistingBarcode_ReturnsResponse() {
        String barcode = "555555";
        Food food = new Food();
        food.setId(UUID.randomUUID().toString());
        food.setName("Eggs");
        FoodResponse expected = FoodResponse.builder().id(food.getId()).name("Eggs").build();

        when(foodRepository.findFoodByBarcodeAndActiveIsTrue(barcode)).thenReturn(Optional.of(food));
        when(foodMapper.toResponse(food)).thenReturn(expected);

        FoodResponse response = foodService.getFoodByBarcode(barcode);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when food by barcode not found")
    void getFoodByBarcode_WithNonExistingBarcode_ThrowsBusinessException() {
        when(foodRepository.findFoodByBarcodeAndActiveIsTrue("000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.getFoodByBarcode("000000"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", FOOD_NOT_FOUND);
    }
}
