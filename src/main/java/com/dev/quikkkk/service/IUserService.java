package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.response.UserResponse;

public interface IUserService {
    UserResponse getCurrentUser();

    UserResponse getUser(String id);
}
