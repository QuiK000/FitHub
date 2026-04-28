package com.dev.quikkkk.modules.workout.dto.response;

import com.dev.quikkkk.modules.user.dto.response.ClientShortResponse;
import com.dev.quikkkk.modules.workout.enums.WaitlistStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaitlistResponse {
    private String id;
    private Integer position;
    private WaitlistStatus status;
    private LocalDateTime joinedAt;
    private ClientShortResponse client;
    private String estimatedWait;
    private String message;
}
