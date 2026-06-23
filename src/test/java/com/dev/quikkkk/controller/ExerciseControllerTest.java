package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.workout.controller.ExerciseController;
import com.dev.quikkkk.modules.workout.dto.response.ExerciseResponse;
import com.dev.quikkkk.modules.workout.service.IExerciseService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@WebMvcTest(
        controllers = ExerciseController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({ExerciseControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class ExerciseControllerTest {

    private static final String BASE_URL = "/api/v1/exercises";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IExerciseService exerciseService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /exercises - admin can create exercise")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createExercise_WithAdminRole_ReturnsCreated() throws Exception {
        ExerciseResponse response = ExerciseResponse.builder()
                .id(UUID.randomUUID().toString())
                .name("Bench Press")
                .build();
        when(exerciseService.createExercise(any())).thenReturn(response);

        String requestBody = """
                {
                  "name": "Bench Press",
                  "description": "A chest exercise",
                  "category": "STRENGTH",
                  "primaryMuscleGroup": "CHEST"
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bench Press"));
    }

    @Test
    @DisplayName("POST /exercises - client is forbidden")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createExercise_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                  "name": "Bench Press",
                  "description": "A chest exercise",
                  "category": "STRENGTH",
                  "primaryMuscleGroup": "CHEST"
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(exerciseService, never()).createExercise(any());
    }

    @Test
    @DisplayName("GET /exercises/{id} - any authenticated user can get exercise")
    @WithMockUser(username = "user", roles = "CLIENT")
    void findExerciseById_WithAuthenticatedUser_ReturnsOk() throws Exception {
        String exerciseId = UUID.randomUUID().toString();
        ExerciseResponse response = ExerciseResponse.builder()
                .id(exerciseId)
                .name("Squat")
                .build();
        when(exerciseService.findExerciseById(exerciseId)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{id}", exerciseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Squat"));
    }

    @Test
    @DisplayName("GET /exercises - returns paginated exercises")
    @WithMockUser(username = "user", roles = "CLIENT")
    void findAllExercises_WithPagination_ReturnsOk() throws Exception {
        ExerciseResponse response = ExerciseResponse.builder()
                .id(UUID.randomUUID().toString())
                .name("Deadlift")
                .build();
        PageResponse<ExerciseResponse> pageResponse = PageResponse.<ExerciseResponse>builder()
                .content(List.of(response))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(exerciseService.findAllExercises(0, 10, null)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("PUT /exercises/{id} - admin can update exercise")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateExercise_WithAdminRole_ReturnsOk() throws Exception {
        String exerciseId = UUID.randomUUID().toString();
        ExerciseResponse response = ExerciseResponse.builder()
                .id(exerciseId)
                .name("Updated Press")
                .build();
        when(exerciseService.updateExercise(eq(exerciseId), any())).thenReturn(response);

        String requestBody = """
                {
                  "name": "Updated Press"
                }
                """;

        mockMvc.perform(put(BASE_URL + "/{id}", exerciseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Press"));
    }

    @Test
    @DisplayName("PATCH /exercises/{id}/activate - admin can activate")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void activateExercise_WithAdminRole_ReturnsOk() throws Exception {
        String exerciseId = UUID.randomUUID().toString();
        when(exerciseService.activateExercise(exerciseId))
                .thenReturn(MessageResponse.builder().message("Exercise activated").build());

        mockMvc.perform(patch(BASE_URL + "/{id}/activate", exerciseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exercise activated"));
    }

    @Test
    @DisplayName("PATCH /exercises/{id}/deactivate - admin can deactivate")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deactivateExercise_WithAdminRole_ReturnsOk() throws Exception {
        String exerciseId = UUID.randomUUID().toString();
        when(exerciseService.deactivateExercise(exerciseId))
                .thenReturn(MessageResponse.builder().message("Exercise deactivated").build());

        mockMvc.perform(patch(BASE_URL + "/{id}/deactivate", exerciseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Exercise deactivated"));
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
