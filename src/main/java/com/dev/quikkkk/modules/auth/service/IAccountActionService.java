package com.dev.quikkkk.modules.auth.service;

import com.dev.quikkkk.modules.auth.dto.request.ResetPasswordRequest;
import com.dev.quikkkk.core.dto.MessageResponse;

public interface IAccountActionService {
    MessageResponse verifyEmail(String token);

    MessageResponse resendVerificationCode(String email);

    MessageResponse forgotPassword(String email);

    MessageResponse resetPassword(ResetPasswordRequest request);
}
