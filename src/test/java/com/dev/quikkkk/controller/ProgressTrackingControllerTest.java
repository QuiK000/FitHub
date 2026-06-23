package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.progress.controller.ProgressTrackingController;
import com.dev.quikkkk.modules.progress.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.modules.progress.dto.response.GoalResponse;
import com.dev.quikkkk.modules.progress.dto.response.PersonalRecordResponse;
import com.dev.quikkkk.modules.progress.service.IBodyMeasurementService;
import com.dev.quikkkk.modules.progress.service.IGoalService;
import com.dev.quikkkk.modules.progress.service.IPersonalRecordService;
import com.dev.quikkkk.modules.progress.service.IProgressPhotoService;
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
        controllers = ProgressTrackingController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({ProgressTrackingControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class ProgressTrackingControllerTest {

    private static final String BASE_URL = "/api/v1/progress";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBodyMeasurementService bodyMeasurementService;

    @MockitoBean
    private IGoalService goalService;

    @MockitoBean
    private IPersonalRecordService personalRecordService;

    @MockitoBean
    private IProgressPhotoService progressPhotoService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /progress/measurements - client can create measurement")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createBodyMeasurement_WithClientRole_ReturnsOk() throws Exception {
        BodyMeasurementResponse response = BodyMeasurementResponse.builder()
                .id(UUID.randomUUID().toString())
                .weight(75.0)
                .build();
        when(bodyMeasurementService.createBodyMeasurement(any())).thenReturn(response);

        String requestBody = """
                {
                  "measurementDate": "2024-01-15T10:00:00",
                  "weight": 75.0
                }
                """;

        mockMvc.perform(post(BASE_URL + "/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(75.0));
    }

    @Test
    @DisplayName("POST /progress/measurements - admin is forbidden")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBodyMeasurement_WithAdminRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                  "measurementDate": "2024-01-15T10:00:00",
                  "weight": 75.0
                }
                """;

        mockMvc.perform(post(BASE_URL + "/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(bodyMeasurementService, never()).createBodyMeasurement(any());
    }

    @Test
    @DisplayName("POST /progress/goals - client can create goal")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createGoal_WithClientRole_ReturnsOk() throws Exception {
        GoalResponse response = GoalResponse.builder()
                .id(UUID.randomUUID().toString())
                .build();
        when(goalService.createGoal(any())).thenReturn(response);

        String requestBody = """
                {
                  "title": "Lose weight",
                  "goalType": "WEIGHT_LOSS",
                  "targetValue": 70.0,
                  "startValue": 80.0,
                  "unit": "KG"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("POST /progress/records - client can create personal record")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createPersonalRecord_WithClientRole_ReturnsOk() throws Exception {
        PersonalRecordResponse response = PersonalRecordResponse.builder()
                .id(UUID.randomUUID().toString())
                .value(100.0)
                .build();
        when(personalRecordService.createPersonalRecord(any())).thenReturn(response);

        String requestBody = """
                {
                  "exerciseId": "%s",
                  "recordType": "MAX_WEIGHT",
                  "value": 100.0,
                  "unit": "KG"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(BASE_URL + "/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(100.0));
    }

    @Test
    @DisplayName("GET /progress/measurements/{id} - client can get measurement")
    @WithMockUser(username = "client", roles = "CLIENT")
    void getBodyMeasurementById_WithClientRole_ReturnsOk() throws Exception {
        String measurementId = UUID.randomUUID().toString();
        BodyMeasurementResponse response = BodyMeasurementResponse.builder()
                .id(measurementId)
                .weight(75.0)
                .build();
        when(bodyMeasurementService.getBodyMeasurementById(measurementId)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/measurements/{id}", measurementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(75.0));
    }

    @Test
    @DisplayName("GET /progress/goals - client can get goals")
    @WithMockUser(username = "client", roles = "CLIENT")
    void getGoals_WithClientRole_ReturnsOk() throws Exception {
        GoalResponse response = GoalResponse.builder()
                .id(UUID.randomUUID().toString())
                .build();
        PageResponse<GoalResponse> pageResponse = PageResponse.<GoalResponse>builder()
                .content(List.of(response))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(goalService.getGoals(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL + "/goals")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("PATCH /progress/goals/{id}/complete - client can complete goal")
    @WithMockUser(username = "client", roles = "CLIENT")
    void completeGoal_WithClientRole_ReturnsOk() throws Exception {
        String goalId = UUID.randomUUID().toString();
        when(goalService.completeGoal(goalId))
                .thenReturn(MessageResponse.builder().message("Goal completed").build());

        mockMvc.perform(patch(BASE_URL + "/goals/{id}/complete", goalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal completed"));
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
