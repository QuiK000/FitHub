package com.dev.quikkkk.modules.app.dto.response;

import com.dev.quikkkk.modules.app.dto.enums.ProfileStatus;
import com.dev.quikkkk.modules.user.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppBootstrapResponse {
    private UserResponse user;
    private Set<String> roles;
    private ProfileStatus profileStatus;
    private boolean onboardingRequired;
    private Set<String> permissions;
}
