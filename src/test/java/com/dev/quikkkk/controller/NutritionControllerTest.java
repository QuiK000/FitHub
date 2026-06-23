package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.nutrition.controller.NutritionController;
import com.dev.quikkkk.modules.nutrition.dto.response.FoodResponse;
import com.dev.quikkkk.modules.nutrition.dto.response.MealPlanResponse;
import com.dev.quikkkk.modules.nutrition.service.IFoodService;
import com.dev.quikkkk.modules.nutrition.service.IMealPlanService;
import com.dev.quikkkk.modules.nutrition.service.IMealService;
import com.dev.quikkkk.modules.nutrition.service.IWaterIntakeService;
import com.dev.quikkkk.core.dto.MessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@WebMvcTest(
        controllers = NutritionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({NutritionControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class NutritionControllerTest {

    private static final String BASE_URL = "/api/v1/nutrition";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFoodService foodService;

    @MockitoBean
    private IMealPlanService mealPlanService;

    @MockitoBean
    private IMealService mealService;

    @MockitoBean
    private IWaterIntakeService waterIntakeService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /nutrition/foods - admin can create food")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createFood_WithAdminRole_ReturnsOk() throws Exception {
        FoodResponse response = FoodResponse.builder()
                .id(UUID.randomUUID().toString())
                .name("Chicken Breast")
                .build();
        when(foodService.createFood(any())).thenReturn(response);

        String requestBody = """
                {
                  "name": "Chicken Breast",
                  "brand": "Tyson",
                  "servingSize": 100.0,
                  "servingUnit": "GRAM",
                  "caloriesPerServing": 165,
                  "macrosPerServing": {
                    "protein": 31.0,
                    "carbs": 0.0,
                    "fats": 3.6
                  },
                  "barcode": "123456"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chicken Breast"));
    }

    @Test
    @DisplayName("POST /nutrition/foods - client is forbidden")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createFood_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                  "name": "Chicken Breast",
                  "brand": "Tyson",
                  "servingSize": 100.0,
                  "servingUnit": "GRAM",
                  "macrosPerServing": {"protein": 31.0, "carbs": 0.0, "fats": 3.6}
                }
                """;

        mockMvc.perform(post(BASE_URL + "/foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(foodService, never()).createFood(any());
    }

    @Test
    @DisplayName("GET /nutrition/foods - any authenticated user can get foods")
    @WithMockUser(username = "user", roles = "CLIENT")
    void getAllFoods_WithAuthenticatedUser_ReturnsOk() throws Exception {
        FoodResponse response = FoodResponse.builder()
                .id(UUID.randomUUID().toString())
                .name("Rice")
                .build();
        PageResponse<FoodResponse> pageResponse = PageResponse.<FoodResponse>builder()
                .content(List.of(response))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(foodService.getAllFoods(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL + "/foods")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /nutrition/foods/{id} - any authenticated user can get food by id")
    @WithMockUser(username = "user", roles = "CLIENT")
    void getFoodById_WithAuthenticatedUser_ReturnsOk() throws Exception {
        String foodId = UUID.randomUUID().toString();
        FoodResponse response = FoodResponse.builder()
                .id(foodId)
                .name("Eggs")
                .build();
        when(foodService.getFoodById(foodId)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/foods/{id}", foodId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eggs"));
    }

    @Test
    @DisplayName("POST /nutrition/meal-plans - client can create meal plan")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createMealPlan_WithClientRole_ReturnsOk() throws Exception {
        MealPlanResponse response = MealPlanResponse.builder()
                .id(UUID.randomUUID().toString())
                .build();
        when(mealPlanService.createMealPlan(any())).thenReturn(response);

        String requestBody = """
                {
                  "planDate": "2024-01-15",
                  "targetCalories": 2000
                }
                """;

        mockMvc.perform(post(BASE_URL + "/meal-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("PATCH /nutrition/foods/{id}/deactivate - admin can deactivate food")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deactivateFood_WithAdminRole_ReturnsOk() throws Exception {
        String foodId = UUID.randomUUID().toString();
        when(foodService.deactivateFood(foodId))
                .thenReturn(MessageResponse.builder().message("Food deactivated").build());

        mockMvc.perform(patch(BASE_URL + "/foods/{id}/deactivate", foodId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Food deactivated"));
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfiguration {
        @Bean
        org.springframework.security.web.SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .build();
        }
    }
}
