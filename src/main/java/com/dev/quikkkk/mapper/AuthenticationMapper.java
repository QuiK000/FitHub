package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.AuthenticationResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationMapper {
    public AuthenticationResponse toResponse(String accessToken, String refreshToken, String tokenType) {
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }
}
