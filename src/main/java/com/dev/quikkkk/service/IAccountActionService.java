package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.ResetPasswordRequest;
import com.dev.quikkkk.dto.response.MessageResponse;

public interface IAccountActionService {
    MessageResponse verifyEmail(String token);

    MessageResponse resendVerificationCode(String email);

    MessageResponse forgotPassword(String email);

    MessageResponse resetPassword(ResetPasswordRequest request);
}
