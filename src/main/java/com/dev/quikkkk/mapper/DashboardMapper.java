package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.ClientAnalyticsResponse;
import com.dev.quikkkk.dto.response.DashboardAnalyticsResponse;
import com.dev.quikkkk.dto.response.PopularSessionResponse;
import com.dev.quikkkk.dto.response.TrainerAnalyticsResponse;
import com.dev.quikkkk.dto.response.TrainerAttendanceMetrics;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.TrainerProfile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardMapper {
    public DashboardAnalyticsResponse dashboardToResponse(
            Integer activeClients,
            Integer activeMemberships,
            BigDecimal revenue,
            Integer todayCheckIns,
            List<PopularSessionResponse> popularSessions
    ) {
        return DashboardAnalyticsResponse.builder()
                .activeClients(activeClients)
                .activeMemberships(activeMemberships)
                .revenue(revenue)
                .todayCheckIns(todayCheckIns)
                .popularSessions(popularSessions)
                .build();
    }

    public TrainerAnalyticsResponse trainerAnalyticsToResponse(
            TrainerProfile trainer,
            TrainerAttendanceMetrics metrics
    ) {
        return TrainerAnalyticsResponse.builder()
                .trainerId(trainer.getId())
                .trainerName(trainer.getFirstname() + " " + trainer.getLastname())
                .totalSessions(metrics.totalSessions())
                .totalClients(metrics.totalClients())
                .attendanceRate(metrics.attendanceRate())
                .build();
    }

    public ClientAnalyticsResponse clientAnalyticsResponse(
            ClientProfile client,
            long totalVisits,
            long missedSessions,
            LocalDateTime lastVisit
    ) {
        return ClientAnalyticsResponse.builder()
                .clientId(client.getId())
                .fullName(client.getFirstname() + " " + client.getLastname())
                .totalVisits(totalVisits)
                .missedSessions(missedSessions)
                .lastVisit(lastVisit)
                .build();
    }
}
