package com.dev.quikkkk.utils;

import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.repository.IWorkoutPlanRepository;
import com.dev.quikkkk.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {
    private final IWorkoutPlanRepository workoutPlanRepository;

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

    public static boolean isAdmin() {
        return currentUser().roles().contains("ROLE_ADMIN");
    }

    public static boolean isTrainer() {
        return currentUser().roles().contains("ROLE_TRAINER");
    }

    @Transactional(readOnly = true)
    public boolean isPlanOwner(String planId) {
        if (isAdmin()) return true;
        Optional<WorkoutPlan> plan = workoutPlanRepository.findById(planId);

        if (plan.isEmpty()) return true;

        String planOwnerId = plan.get().getTrainer().getUser().getId();
        return planOwnerId.equals(getCurrentUserId());
    }
}
