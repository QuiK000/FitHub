package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.Positive;
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
public class UpdateGoalProgressRequest {
    @Positive(message = "VALIDATION.GOAL.CURRENT_VALUE.POSITIVE")
    private Double currentValue;
    private String notes;
}
