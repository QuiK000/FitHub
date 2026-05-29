package com.dev.quikkkk.core.config;

import com.dev.quikkkk.core.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/signup",
            "/api/v1/auth/signin",
            "/api/v1/auth/logout",

            "/api/v1/account-action/verify-email",
            "/api/v1/account-action/resend-verification",
            "/api/v1/account-action/forgot-password",
            "/api/v1/account-action/reset-password",

            "/api/v1/telegram/webhook",

            "/v3/api-docs/**",

            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",

            "/webjars/**",

            "/actuator/health",
            "/actuator/health/**",
            "/actuator/prometheus"
    };

    private final JwtFilter filter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .cors(cors -> {
                })
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request,
                                                   response,
                                                   authException
                        ) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            Map<String, Object> body = Map.of(
                                    "status", 401,
                                    "error", "UNAUTHORIZED",
                                    "message", "Authentication required",
                                    "timestamp", LocalDateTime.now().toString()
                            );

                            objectMapper.writeValue(response.getWriter(), body);
                        }).accessDeniedHandler((request,
                                                response,
                                                accessDeniedException
                        ) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            Map<String, Object> body = Map.of(
                                    "status", 403,
                                    "error", "FORBIDDEN",
                                    "message", "You don't have permission to access this resource",
                                    "timestamp", LocalDateTime.now().toString()
                            );

                            objectMapper.writeValue(response.getWriter(), body);
                        }))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PUBLIC_URLS)
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true))
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentTypeOptions(content -> {
                        })
                        .referrerPolicy(referrer ->
                                referrer.policy(
                                        ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                                )))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
