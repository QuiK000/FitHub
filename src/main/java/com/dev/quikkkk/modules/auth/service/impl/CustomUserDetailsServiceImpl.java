package com.dev.quikkkk.modules.auth.service.impl;

import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
import com.dev.quikkkk.core.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final IUserRepository repository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new SecurityUser(user);
    }
}
