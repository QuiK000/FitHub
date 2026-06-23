package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.membership.controller.MembershipController;
import com.dev.quikkkk.modules.membership.dto.response.MembershipResponse;
import com.dev.quikkkk.modules.membership.enums.MembershipStatus;
import com.dev.quikkkk.modules.membership.enums.MembershipType;
import com.dev.quikkkk.modules.membership.service.IMembershipService;
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
        controllers = MembershipController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({MembershipControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class MembershipControllerTest {

    private static final String BASE_URL = "/api/v1/memberships";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IMembershipService membershipService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /memberships - admin can create membership")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createMembership_WithAdminRole_ReturnsCreated() throws Exception {
        MembershipResponse response = MembershipResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(MembershipType.MONTHLY)
                .status(MembershipStatus.CREATED)
                .build();
        when(membershipService.createMembership(any())).thenReturn(response);

        String requestBody = """
                {
                  "clientId": "%s",
                  "type": "MONTHLY",
                  "durationMonths": 1
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("MONTHLY"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("POST /memberships - client is forbidden")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createMembership_WithClientRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                  "clientId": "%s",
                  "type": "MONTHLY"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(membershipService, never()).createMembership(any());
    }

    @Test
    @DisplayName("PATCH /memberships/{id}/activate - admin can activate")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void activateMembership_WithAdminRole_ReturnsOk() throws Exception {
        String membershipId = UUID.randomUUID().toString();
        MembershipResponse response = MembershipResponse.builder()
                .id(membershipId)
                .status(MembershipStatus.ACTIVE)
                .build();
        when(membershipService.activateMembership(membershipId)).thenReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{id}/activate", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PATCH /memberships/{id}/freeze - admin can freeze")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void freezeMembership_WithAdminRole_ReturnsOk() throws Exception {
        String membershipId = UUID.randomUUID().toString();
        MembershipResponse response = MembershipResponse.builder()
                .id(membershipId)
                .status(MembershipStatus.FROZEN)
                .build();
        when(membershipService.freezeMembership(membershipId)).thenReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{id}/freeze", membershipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FROZEN"));
    }

    @Test
    @DisplayName("GET /memberships/me/active - client can get active membership")
    @WithMockUser(username = "client", roles = "CLIENT")
    void getActiveMembership_WithClientRole_ReturnsOk() throws Exception {
        MembershipResponse response = MembershipResponse.builder()
                .id(UUID.randomUUID().toString())
                .type(MembershipType.MONTHLY)
                .status(MembershipStatus.ACTIVE)
                .build();
        when(membershipService.getMembershipByClientIdAndActive()).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/me/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /memberships/me/active - trainer is forbidden")
    @WithMockUser(username = "trainer", roles = "TRAINER")
    void getActiveMembership_WithTrainerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL + "/me/active"))
                .andExpect(status().isForbidden());

        verify(membershipService, never()).getMembershipByClientIdAndActive();
    }

    @Test
    @DisplayName("GET /memberships/client/{id} - admin can get by client")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getMembershipByClient_WithAdminRole_ReturnsOk() throws Exception {
        String clientId = UUID.randomUUID().toString();
        MembershipResponse response = MembershipResponse.builder()
                .id(UUID.randomUUID().toString())
                .build();
        PageResponse<MembershipResponse> pageResponse = PageResponse.<MembershipResponse>builder()
                .content(List.of(response))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(membershipService.getMembershipByClientId(0, 10, clientId)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL + "/client/{id}", clientId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
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
