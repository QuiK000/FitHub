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
public class LogWaterIntakeRequest {
    @Positive(message = "VALIDATION.WATER.AMOUNT.POSITIVE")
    private Integer amountMl;
}
