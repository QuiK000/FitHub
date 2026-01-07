package com.dev.quikkkk.security;

import com.dev.quikkkk.entity.User;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SecurityUser implements UserDetails {
    @Getter
    private final UserPrincipal principal;
    private final String password;
    private final boolean enabled;

    public SecurityUser(User user) {
        this.principal = UserPrincipal.from(user);
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
    }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Override
    @NullMarked
    public String getUsername() {
        return principal.email();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
