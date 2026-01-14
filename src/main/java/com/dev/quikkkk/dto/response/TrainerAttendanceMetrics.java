package com.dev.quikkkk.dto.response;

import lombok.Builder;

@Builder
public record TrainerAttendanceMetrics(long totalSessions, long totalClients, double attendanceRate) {
}
