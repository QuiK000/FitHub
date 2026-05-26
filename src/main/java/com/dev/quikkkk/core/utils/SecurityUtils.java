package com.dev.quikkkk.core.utils;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.security.UserPrincipal;
import com.dev.quikkkk.modules.workout.entity.WorkoutPlan;
import com.dev.quikkkk.modules.workout.repository.IWorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        return currentUser().roles().contains("ADMIN");
    }

    public static boolean isTrainer() {
        return currentUser().roles().contains("TRAINER");
    }

    public static boolean isClient() {
        return currentUser().roles().contains("CLIENT");
    }

    @Transactional(readOnly = true)
    public boolean isPlanOwner(String planId) {
        if (isAdmin()) return true;
        WorkoutPlan plan = workoutPlanRepository
                .findById(planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORKOUT_PLAN_NOT_FOUND));

        return plan.getTrainer().getUser().getId().equals(getCurrentUserId());
    }
}
