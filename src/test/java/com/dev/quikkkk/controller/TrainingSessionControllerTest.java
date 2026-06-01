package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.user.dto.response.TrainerShortResponse;
import com.dev.quikkkk.modules.workout.controller.TrainingSessionController;
import com.dev.quikkkk.modules.workout.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.modules.workout.enums.TrainingStatus;
import com.dev.quikkkk.modules.workout.enums.TrainingType;
import com.dev.quikkkk.modules.workout.service.ITrainingSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@WebMvcTest(
        controllers = TrainingSessionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({TrainingSessionControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class TrainingSessionControllerTest {

    private static final String BASE_URL = "/api/v1/sessions";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ITrainingSessionService trainingSessionService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(username = "trainer", roles = "TRAINER")
    void createTrainingSession_validTrainerRequest_returnsCreatedSession() throws Exception {
        // given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);
        TrainingSessionResponse response = sessionResponse(startTime, endTime);
        when(trainingSessionService.createSession(any())).thenReturn(response);
        String requestBody = """
                {
                  "type": "GROUP",
                  "startTime": "%s",
                  "endTime": "%s",
                  "maxParticipants": 10
                }
                """.formatted(format(startTime), format(endTime));

        // when
        // then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.type").value("GROUP"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.maxParticipants").value(10))
                .andExpect(jsonPath("$.currentParticipants").value(0))
                .andExpect(jsonPath("$.trainer.trainerId").value("trainer-id"))
                .andExpect(jsonPath("$.trainer.firstname").value("Jane"))
                .andExpect(jsonPath("$.trainer.lastname").value("Coach"));

        verify(trainingSessionService).createSession(any());
    }

    @Test
    @WithMockUser(username = "trainer", roles = "TRAINER")
    void createTrainingSession_missingRequiredFields_returnsBadRequest() throws Exception {
        // given
        String requestBody = """
                {
                  "maxParticipants": 10
                }
                """;

        // when
        // then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'type')]").exists())
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'startTime')]").exists())
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'endTime')]").exists());

        verify(trainingSessionService, never()).createSession(any());
    }

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void createTrainingSession_authenticatedClient_returnsForbidden() throws Exception {
        // given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        String requestBody = """
                {
                  "type": "GROUP",
                  "startTime": "%s",
                  "endTime": "%s",
                  "maxParticipants": 10
                }
                """.formatted(format(startTime), format(startTime.plusHours(1)));

        // when
        // then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        verify(trainingSessionService, never()).createSession(any());
    }

    @Test
    @WithMockUser(username = "user", roles = "CLIENT")
    void getAllTrainingSessions_validPagination_returnsSessionPage() throws Exception {
        // given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        TrainingSessionResponse session = sessionResponse(startTime, startTime.plusHours(1));
        PageResponse<TrainingSessionResponse> response = PageResponse.<TrainingSessionResponse>builder()
                .content(List.of(session))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(trainingSessionService.getTrainingSessions(0, 10, "group")).thenReturn(response);

        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "group"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(session.getId()))
                .andExpect(jsonPath("$.content[0].type").value("GROUP"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(trainingSessionService).getTrainingSessions(0, 10, "group");
    }

    @Test
    @WithMockUser(username = "trainer", roles = "TRAINER")
    void updateTrainingSession_validRequest_returnsUpdatedSession() throws Exception {
        // given
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now().plusDays(2);
        TrainingSessionResponse response = sessionResponse(startTime, startTime.plusHours(2));
        response.setId(sessionId);
        response.setMaxParticipants(12);
        when(trainingSessionService.updateSession(eq(sessionId), any())).thenReturn(response);
        String requestBody = """
                {
                  "starTime": "%s",
                  "endTime": "%s",
                  "maxParticipants": 12
                }
                """.formatted(format(startTime), format(startTime.plusHours(2)));

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{session-id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.maxParticipants").value(12))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(trainingSessionService).updateSession(eq(sessionId), any());
    }

    @Test
    @WithMockUser(username = "trainer", roles = "TRAINER")
    void updateTrainingSession_nonExistentSession_returnsNotFound() throws Exception {
        // given
        String sessionId = UUID.randomUUID().toString();
        when(trainingSessionService.updateSession(eq(sessionId), any()))
                .thenThrow(new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        String requestBody = """
                {
                  "maxParticipants": 12
                }
                """;

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{session-id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SESSION_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Session Not Found"));

        verify(trainingSessionService).updateSession(eq(sessionId), any());
    }

    @Test
    @WithMockUser(username = "trainer", roles = "TRAINER")
    void updateTrainingSession_maxParticipantsBelowMinimum_returnsBadRequest() throws Exception {
        // given
        String sessionId = UUID.randomUUID().toString();
        String requestBody = """
                {
                  "maxParticipants": 0
                }
                """;

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{session-id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors[0].field").value("maxParticipants"));

        verify(trainingSessionService, never()).updateSession(any(), any());
    }

    private TrainingSessionResponse sessionResponse(LocalDateTime startTime, LocalDateTime endTime) {
        return TrainingSessionResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(TrainingType.GROUP)
                .status(TrainingStatus.SCHEDULED)
                .startTime(startTime)
                .endTime(endTime)
                .maxParticipants(10)
                .currentParticipants(0)
                .trainer(TrainerShortResponse.builder()
                        .trainerId("trainer-id")
                        .firstname("Jane")
                        .lastname("Coach")
                        .build())
                .build();
    }

    private String format(LocalDateTime dateTime) {
        return dateTime.withNano(0).toString().replace('T', ' ');
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfiguration {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .build();
        }
    }
}
