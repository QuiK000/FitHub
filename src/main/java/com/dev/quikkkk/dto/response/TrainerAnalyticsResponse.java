package com.dev.quikkkk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TrainerAnalyticsResponse {
    private String trainerId;
    private String trainerName;
    private long totalSessions;
    private long totalClients;
    private double attendanceRate;
}
