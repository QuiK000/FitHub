package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.notification.controller.NotificationController;
import com.dev.quikkkk.modules.notification.dto.response.NotificationResponse;
import com.dev.quikkkk.modules.notification.dto.response.NotificationSummaryResponse;
import com.dev.quikkkk.modules.notification.realtime.INotificationRealtimeService;
import com.dev.quikkkk.modules.notification.service.INotificationService;
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
        controllers = NotificationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({NotificationControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class NotificationControllerTest {

    private static final String BASE_URL = "/api/v1/notifications";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private INotificationService notificationService;

    @MockitoBean
    private INotificationRealtimeService notificationRealtimeService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("GET /notifications - authenticated user can get notifications")
    @WithMockUser(username = "user", roles = "CLIENT")
    void getNotifications_WithAuthenticatedUser_ReturnsOk() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
                .id(UUID.randomUUID().toString())
                .title("Test")
                .build();
        PageResponse<NotificationResponse> pageResponse = PageResponse.<NotificationResponse>builder()
                .content(List.of(response))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(notificationService.findAllNotifications(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /notifications/unread/count - returns unread count")
    @WithMockUser(username = "user", roles = "CLIENT")
    void getUnreadCount_WithAuthenticatedUser_ReturnsCount() throws Exception {
        when(notificationService.getUnreadCount()).thenReturn(5L);

        mockMvc.perform(get(BASE_URL + "/unread/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    @DisplayName("PATCH /notifications/{id}/read - marks notification as read")
    @WithMockUser(username = "user", roles = "CLIENT")
    void markAsRead_WithValidNotificationId_ReturnsOk() throws Exception {
        String notificationId = UUID.randomUUID().toString();
        when(notificationService.readNotification(notificationId))
                .thenReturn(MessageResponse.builder().message("Marked as read").build());

        mockMvc.perform(patch(BASE_URL + "/{id}/read", notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Marked as read"));
    }

    @Test
    @DisplayName("POST /notifications/send - admin can send notification")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void sendNotification_WithAdminRole_ReturnsOk() throws Exception {
        when(notificationService.sendNotification(any()))
                .thenReturn(MessageResponse.builder().message("Notification sent").build());

        String requestBody = """
                {
                  "recipientId": "%s",
                  "title": "Test",
                  "message": "Hello",
                  "type": "GENERAL_ANNOUNCEMENT",
                  "priority": "LOW"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(BASE_URL + "/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notification sent"));
    }

    @Test
    @DisplayName("POST /notifications/send - client is forbidden")
    @WithMockUser(username = "client", roles = "CLIENT")
    void sendNotification_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                  "recipientId": "%s",
                  "title": "Test",
                  "message": "Hello",
                  "type": "GENERAL_ANNOUNCEMENT",
                  "priority": "LOW"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(BASE_URL + "/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(notificationService, never()).sendNotification(any());
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
