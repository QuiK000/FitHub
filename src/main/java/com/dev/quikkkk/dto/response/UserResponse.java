package com.dev.quikkkk.dto.response;

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
public class UserResponse {
    private String id;
    private String email;
    private boolean enabled;
    private Set<String> roles;
    private TrainerProfileResponse trainerProfile;
    private ClientProfileResponse clientProfile;
}
