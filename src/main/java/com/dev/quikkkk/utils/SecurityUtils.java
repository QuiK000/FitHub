package com.dev.quikkkk.utils;

import com.dev.quikkkk.security.SecurityUser;
import com.dev.quikkkk.security.UserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static UserPrincipal currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) return null;
        Object principal = auth.getPrincipal();

        if (principal instanceof SecurityUser securityUser) return securityUser.getPrincipal();
        return null;
    }
}
