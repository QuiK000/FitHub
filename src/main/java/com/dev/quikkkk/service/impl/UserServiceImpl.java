package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.UserResponse;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.UserMapper;
import com.dev.quikkkk.repository.IUserRepository;
import com.dev.quikkkk.service.IUserService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.UNAUTHORIZED_USER;
import static com.dev.quikkkk.enums.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Cacheable(value = "users", key = "#root.method.name + ':' + T(com.dev.quikkkk.utils.SecurityUtils).getCurrentUserId()")
    public UserResponse getCurrentUser() {
        log.info("Fetching current authenticated user");
        return userMapper.toResponse(getCurrentUserEntity());
    }

    @Override
    @Cacheable(value = "users", key = "'byId:' + #id")
    public UserResponse getUser(String id) {
        log.info("Fetching user by id={}", id);
        return userMapper.toResponse(findUserById(id));
    }

    private User getCurrentUserEntity() {
        String userId = SecurityUtils.getCurrentUserId();

        if (userId == null) {
            log.warn("Attempt to access current user without authentication");
            throw new BusinessException(UNAUTHORIZED_USER);
        }

        return findUserById(userId);
    }

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found, id={}", id);
                    return new BusinessException(USER_NOT_FOUND);
                });
    }
}
