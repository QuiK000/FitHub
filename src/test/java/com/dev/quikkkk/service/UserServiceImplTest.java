package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.utils.SecurityUtils;
import com.dev.quikkkk.modules.user.dto.response.UserResponse;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.mapper.UserMapper;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
import com.dev.quikkkk.modules.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceImplTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should get current user successfully")
    void getCurrentUser_WithValidUserId_ReturnsUserResponse() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            String userId = UUID.randomUUID().toString();
            security.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            User user = User.builder().id(userId).email("test@test.com").enabled(true).roles(Set.of()).build();
            UserResponse expected = UserResponse.builder().id(userId).email("test@test.com").build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(expected);

            UserResponse response = userService.getCurrentUser();

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(userId);
            verify(userRepository).findById(userId);
        }
    }

    @Test
    @DisplayName("Should throw exception when user is not authenticated")
    void getCurrentUser_WithNullUserId_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            security.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED_USER);
        }
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getCurrentUser_WithNonExistingUserId_ThrowsBusinessException() {
        try (MockedStatic<SecurityUtils> security = mockStatic(SecurityUtils.class)) {
            String userId = UUID.randomUUID().toString();
            security.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void getUser_WithValidId_ReturnsUserResponse() {
        String userId = UUID.randomUUID().toString();
        User user = User.builder().id(userId).email("test@test.com").enabled(true).build();
        UserResponse expected = UserResponse.builder().id(userId).email("test@test.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponse response = userService.getUser(userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should throw exception when user by id not found")
    void getUser_WithNonExistingId_ThrowsBusinessException() {
        String userId = UUID.randomUUID().toString();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}
