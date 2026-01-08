package com.dev.quikkkk.utils;

import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.dev.quikkkk.enums.ErrorCode.USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ServiceUtils {
    private final IUserRepository userRepository;

    public User getUserByIdOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, userId));
    }

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }
}
