package com.dev.quikkkk.modules.auth.service;

import com.dev.quikkkk.modules.auth.dto.request.LoginRequest;
import com.dev.quikkkk.modules.auth.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.modules.auth.dto.request.RegistrationRequest;
import com.dev.quikkkk.modules.auth.dto.response.AuthenticationResponse;
import com.dev.quikkkk.core.dto.MessageResponse;

public interface IAuthenticationService {
    AuthenticationResponse login(LoginRequest request, String ipAddress);

    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    MessageResponse register(RegistrationRequest request);

    MessageResponse logout(String accessToken);
}
