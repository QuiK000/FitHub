package com.dev.quikkkk.core.security;

import com.dev.quikkkk.modules.user.entity.Role;
import com.dev.quikkkk.modules.user.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserPrincipal(
        String id,
        String email,
        Set<String> roles
) {
    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }
}
