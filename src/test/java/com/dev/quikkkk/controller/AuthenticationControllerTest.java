package com.dev.quikkkk.controller;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.exception.GlobalExceptionHandler;
import com.dev.quikkkk.core.security.JwtFilter;
import com.dev.quikkkk.core.utils.RateLimitInterceptor;
import com.dev.quikkkk.modules.auth.controller.AuthenticationController;
import com.dev.quikkkk.modules.auth.dto.request.LoginRequest;
import com.dev.quikkkk.modules.auth.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.modules.auth.dto.request.RegistrationRequest;
import com.dev.quikkkk.modules.auth.dto.response.AuthenticationResponse;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.modules.auth.service.IAccountActionService;
import com.dev.quikkkk.modules.auth.service.IAuthenticationService;
import com.dev.quikkkk.modules.workout.controller.TrainingSessionController;
import com.dev.quikkkk.modules.workout.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.modules.workout.enums.TrainingStatus;
import com.dev.quikkkk.modules.workout.enums.TrainingType;
import com.dev.quikkkk.modules.workout.service.ITrainingSessionService;
import com.dev.quikkkk.modules.user.dto.response.TrainerShortResponse;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.dev.quikkkk.core.enums.ErrorCode.ACCOUNT_DISABLED;
import static com.dev.quikkkk.core.enums.ErrorCode.EMAIL_ALREADY_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@WebMvcTest(
        controllers = AuthenticationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {JwtFilter.class, RateLimitInterceptor.class}
        )
)
@Import({AuthenticationControllerTest.TestSecurityConfiguration.class, GlobalExceptionHandler.class})
class AuthenticationControllerTest {

    private static final String BASE_URL = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuthenticationService authenticationService;

    @MockitoBean
    private IAccountActionService accountActionService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("POST /auth/signin - should return tokens on valid credentials")
    void signIn_WithValidCredentials_ReturnsTokens() throws Exception {
        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer ")
                .build();
        when(authenticationService.login(any(LoginRequest.class), any())).thenReturn(response);

        String requestBody = """
                {
                  "email": "user@test.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer "));

        verify(authenticationService).login(any(LoginRequest.class), any());
    }

    @Test
    @DisplayName("POST /auth/signin - should return 401 on disabled account")
    void signIn_WithDisabledAccount_ReturnsForbidden() throws Exception {
        when(authenticationService.login(any(LoginRequest.class), any()))
                .thenThrow(new BusinessException(ACCOUNT_DISABLED));

        String requestBody = """
                {
                  "email": "disabled@test.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCOUNT_DISABLED"));
    }

    @Test
    @DisplayName("POST /auth/signup - should create user and return success")
    void signUp_WithValidData_ReturnsSuccess() throws Exception {
        when(authenticationService.register(any(RegistrationRequest.class)))
                .thenReturn(MessageResponse.builder().message("User registered successfully").build());

        String requestBody = """
                {
                  "email": "newuser@test.com",
                  "password": "StrongP@ss123",
                  "confirmPassword": "StrongP@ss123"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(authenticationService).register(any(RegistrationRequest.class));
    }

    @Test
    @DisplayName("POST /auth/signup - should return 409 on duplicate email")
    void signUp_WithDuplicateEmail_ReturnsConflict() throws Exception {
        when(authenticationService.register(any(RegistrationRequest.class)))
                .thenThrow(new BusinessException(EMAIL_ALREADY_EXISTS));

        String requestBody = """
                {
                  "email": "existing@test.com",
                  "password": "StrongP@ss123",
                  "confirmPassword": "StrongP@ss123"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("POST /auth/signup - should return 400 on missing fields")
    void signUp_WithMissingFields_ReturnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "email": "test@test.com"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(authenticationService, never()).register(any());
    }

    @Test
    @DisplayName("POST /auth/refresh-token - should return new tokens")
    void refreshToken_WithValidToken_ReturnsNewTokens() throws Exception {
        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer ")
                .build();
        when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        String requestBody = """
                {
                  "refreshToken": "old.refresh.token"
                }
                """;

        mockMvc.perform(post(BASE_URL + "/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("POST /auth/logout - should return success")
    void logout_WithValidToken_ReturnsSuccess() throws Exception {
        when(authenticationService.logout(any()))
                .thenReturn(MessageResponse.builder().message("User logged out.").build());

        mockMvc.perform(post(BASE_URL + "/logout")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out."));
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfiguration {
        @Bean
        org.springframework.security.web.SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }
}
