package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.RegistrationRequest;
import com.dev.quikkkk.dto.response.UserResponse;
import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final TrainerProfileMapper trainerProfileMapper;
    private final ClientProfileMapper clientProfileMapper;

    public User toUser(RegistrationRequest request, Set<Role> roles) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .enabled(false)
                .createdBy("SYSTEM")
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .trainerProfile(
                        user.getTrainerProfile() != null
                                ? trainerProfileMapper.toResponse(user.getTrainerProfile())
                                : null
                )
                .clientProfile(
                        user.getClientProfile() != null
                                ? clientProfileMapper.toResponse(user.getClientProfile())
                                : null
                )
                .build();
    }
}
