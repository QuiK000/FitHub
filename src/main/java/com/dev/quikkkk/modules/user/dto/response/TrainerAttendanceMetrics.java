package com.dev.quikkkk.modules.user.dto.response;

import lombok.Builder;

@Builder
public record TrainerAttendanceMetrics(long totalSessions, long totalClients, double attendanceRate) {
}
