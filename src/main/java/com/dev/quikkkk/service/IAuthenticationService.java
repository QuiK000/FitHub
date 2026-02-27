package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LoginRequest;
import com.dev.quikkkk.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.dto.request.RegistrationRequest;
import com.dev.quikkkk.dto.response.AuthenticationResponse;
import com.dev.quikkkk.dto.response.MessageResponse;

public interface IAuthenticationService {
    AuthenticationResponse login(LoginRequest request, String ipAddress);

    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    MessageResponse register(RegistrationRequest request);

    MessageResponse logout(String accessToken);
}
