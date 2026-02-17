package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.GoalType;
import com.dev.quikkkk.enums.MeasurementUnit;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGoalRequest {
    @Size(max = 200, message = "VALIDATION.GOAL.TITLE.SIZE")
    private String title;
    private String description;
    private GoalType goalType;

    @Positive(message = "VALIDATION.GOAL.START_VALUE.POSITIVE")
    private Double startValue;

    @Positive(message = "VALIDATION.GOAL.TARGET_VALUE.POSITIVE")
    private Double targetValue;

    @Positive(message = "VALIDATION.GOAL.CURRENT_VALUE.POSITIVE")
    private Double currentValue;
    private MeasurementUnit unit;

    @Future(message = "VALIDATION.GOAL.TARGET_DATE.FUTURE")
    private LocalDateTime targetDate;
    private String notes;
}
