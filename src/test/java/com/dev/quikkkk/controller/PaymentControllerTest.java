package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.membership.controller.PaymentController;
import com.dev.quikkkk.modules.membership.dto.response.PaymentResponse;
import com.dev.quikkkk.modules.membership.service.IPaymentService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@WebMvcTest(
        controllers = PaymentController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({PaymentControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class PaymentControllerTest {

    private static final String BASE_URL = "/api/v1/payments";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPaymentService paymentService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /payments - client can create payment")
    @WithMockUser(username = "client", roles = "CLIENT")
    void createPayment_WithClientRole_ReturnsOk() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .amount(new BigDecimal("100.00"))
                .build();
        when(paymentService.createPayment(any())).thenReturn(response);

        String requestBody = """
                {
                  "membershipId": "%s",
                  "currency": "TRX",
                  "transactionHash": "test-hash-123",
                  "amount": 100.00
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    @DisplayName("POST /payments - admin is forbidden")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createPayment_WithAdminRole_ReturnsForbidden() throws Exception {
        String requestBody = """
                {
                  "membershipId": "%s",
                  "currency": "TRX",
                  "transactionHash": "hash",
                  "amount": 100.00
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());

        verify(paymentService, never()).createPayment(any());
    }

    @Test
    @DisplayName("GET /payments/me - client can get their payments")
    @WithMockUser(username = "client", roles = "CLIENT")
    void getPayments_WithClientRole_ReturnsOk() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .amount(new BigDecimal("50.00"))
                .build();
        PageResponse<PaymentResponse> pageResponse = PageResponse.<PaymentResponse>builder()
                .content(List.of(response))
                .totalElements(1)
                .totalPages(1)
                .number(0)
                .size(10)
                .build();
        when(paymentService.getPayments(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL + "/me")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /payments/client/{id} - admin can get payments by client")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getPaymentsByClient_WithAdminRole_ReturnsOk() throws Exception {
        String clientId = UUID.randomUUID().toString();
        PageResponse<PaymentResponse> pageResponse = PageResponse.<PaymentResponse>builder()
                .content(List.of())
                .totalElements(0)
                .totalPages(0)
                .number(0)
                .size(10)
                .build();
        when(paymentService.getPaymentsByClientId(clientId, 0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URL + "/client/{id}", clientId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
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
