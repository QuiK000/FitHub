package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.review.controller.ClientReviewController;
import com.dev.quikkkk.modules.review.dto.response.TrainerReviewResponse;
import com.dev.quikkkk.modules.review.service.IClientReviewService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@WebMvcTest(
        controllers = ClientReviewController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({ClientReviewControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class ClientReviewControllerTest {

    private static final String BASE_URL = "/api/v1/reviews";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IClientReviewService clientReviewService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /reviews/trainers/{id} - client can create review")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createReview_WithClientRole_ReturnsOk() throws Exception {
        String trainerId = UUID.randomUUID().toString();
        TrainerReviewResponse response = TrainerReviewResponse.builder()
                .id(UUID.randomUUID().toString())
                .rating(5)
                .build();
        when(clientReviewService.createReview(any(), eq(trainerId))).thenReturn(response);

        String requestBody = """
                {
                  "rating": 5,
                  "comment": "Great trainer!"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/trainers/{trainerId}", trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("POST /reviews/trainers/{id} - admin is forbidden")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createReview_WithAdminRole_ReturnsForbidden() throws Exception {
        String trainerId = UUID.randomUUID().toString();
        String requestBody = """
                {
                  "rating": 5,
                  "comment": "Great!"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/trainers/{trainerId}", trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(clientReviewService, never()).createReview(any(), any());
    }

    @Test
    @DisplayName("GET /reviews/my-reviews - client can get their reviews")
    @WithMockUser(username = "client", roles = "CLIENT")
    void getReviews_WithClientRole_ReturnsOk() throws Exception {
        TrainerReviewResponse response = TrainerReviewResponse.builder()
                .id(UUID.randomUUID().toString())
                .rating(5)
                .build();
        PageResponse<TrainerReviewResponse> pageResponse = PageResponse.<TrainerReviewResponse>builder()
                .content(List.of(response))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(clientReviewService.getReviews(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL + "/my-reviews")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("PUT /reviews/{id} - client can update their review")
    @WithMockUser(username = "client", roles = "CLIENT")
    void updateReview_WithClientRole_ReturnsOk() throws Exception {
        String reviewId = UUID.randomUUID().toString();
        when(clientReviewService.updateReview(eq(reviewId), any()))
                .thenReturn(MessageResponse.builder().message("Review updated").build());

        String requestBody = """
                {
                  "rating": 4,
                  "comment": "Updated comment"
                }
                """;

        mockMvc.perform(put(BASE_URL + "/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Review updated"));
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
