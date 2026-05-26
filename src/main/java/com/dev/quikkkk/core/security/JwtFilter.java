package com.dev.quikkkk.core.security;

import com.dev.quikkkk.modules.auth.enums.JwtTokenType;
import com.dev.quikkkk.modules.auth.service.IJwtService;
import com.dev.quikkkk.modules.auth.service.ITokenBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final IJwtService jwtService;
    private final ITokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null
                || !authHeader.startsWith(BEARER_PREFIX)
                || authHeader.length() == BEARER_PREFIX.length()
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(BEARER_PREFIX.length()).trim();

            if (!JwtTokenType.ACCESS.name().equals(jwtService.extractTokenType(jwt))) {
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtService.isTokenExpired(jwt)) {
                log.debug("JWT token is expired for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                log.debug("JWT token is blacklisted for request: {}", request.getRequestURI());
                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(jwt);
            String userId = jwtService.extractUserId(jwt);

            var roles = jwtService.extractRoles(jwt);
            var authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

            var principal = new UserPrincipal(userId, email, Set.copyOf(roles));
            var authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.trace("Authenticated user '{}' for request: {}", email, request.getRequestURI());
        } catch (JwtException e) {
            log.debug("Invalid JWT token for request {}: {}", request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
