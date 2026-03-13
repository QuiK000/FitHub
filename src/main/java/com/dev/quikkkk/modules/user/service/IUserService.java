package com.dev.quikkkk.modules.user.service;

import com.dev.quikkkk.modules.user.dto.response.UserResponse;

public interface IUserService {
    UserResponse getCurrentUser();

    UserResponse getUser(String id);
}
