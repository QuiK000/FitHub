package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.ClientWorkoutStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ClientWorkoutPlanResponse {
    private String id;
    private WorkoutPlanShortResponse workoutPlan;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private ClientWorkoutStatus status;
    private Double completionPercentage;
    private Integer totalWorkouts;
    private Integer completedWorkouts;
}
