package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.response.MessageResponse;

public interface IAccountActionService {
    MessageResponse verifyEmail(String token);

    MessageResponse resendVerificationCode(String email);
}
