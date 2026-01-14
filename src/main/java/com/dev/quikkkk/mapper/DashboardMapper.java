package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.DashboardAnalyticsResponse;
import com.dev.quikkkk.dto.response.PopularSessionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class DashboardMapper {
    public DashboardAnalyticsResponse toResponse(
            Integer activeClients,
            Integer activeMemberships,
            BigDecimal revenue,
            Integer todayCheckIns,
            Set<PopularSessionResponse> popularSessions
    ) {
        return DashboardAnalyticsResponse.builder()
                .activeClients(activeClients)
                .activeMemberships(activeMemberships)
                .revenue(revenue)
                .todayCheckIns(todayCheckIns)
                .popularSessions(popularSessions)
                .build();
    }
}
