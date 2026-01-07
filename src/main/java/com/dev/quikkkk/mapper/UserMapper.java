package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.RegistrationRequest;
import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User toUser(RegistrationRequest request, Set<Role> roles) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .enabled(false)
                .createdBy("SYSTEM")
                .build();
    }
}
