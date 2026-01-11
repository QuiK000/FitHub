package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CheckInTrainingSessionRequest {
    @NotBlank(message = "VALIDATION.TRAINING.CLIENT_ID.NOT_BLANK")
    private String clientId;
}
