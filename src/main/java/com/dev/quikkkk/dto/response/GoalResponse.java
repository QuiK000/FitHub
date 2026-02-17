package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.GoalStatus;
import com.dev.quikkkk.enums.GoalType;
import com.dev.quikkkk.enums.MeasurementUnit;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GoalResponse {
    private String id;
    private String title;
    private String description;
    private GoalType goalType;
    private Double targetValue;
    private Double currentValue;
    private Double startValue;
    private MeasurementUnit unit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime targetDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completionDate;
    private GoalStatus status;
    private Double progressPercentage;
    private String notes;
    private Integer daysRemaining;
    private Double averageProgressPerDay;
}
