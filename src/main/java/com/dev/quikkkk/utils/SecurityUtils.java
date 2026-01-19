package com.dev.quikkkk.utils;

import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static UserPrincipal currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal))
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);

        return principal;
    }

    public static String getCurrentUserId() {
        return currentUser().id();
    }

    public static UserPrincipal currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) return null;
        return principal;
    }

    public static String getCurrentUserIdOrNull() {
        UserPrincipal user = currentUserOrNull();
        return user != null ? user.id() : null;
    }
}
