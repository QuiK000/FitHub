package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinTrainingSessionRequest {
    @NotNull(message = "VALIDATION.TRAINING_SESSION.JOIN.CONFIRM_REQUIRED")
    private Boolean confirm;
}
