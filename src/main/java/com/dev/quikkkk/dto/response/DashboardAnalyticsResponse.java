package com.dev.quikkkk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardAnalyticsResponse {
    private Integer activeClients;
    private Integer activeMemberships;
    private BigDecimal revenue;
    private Integer todayCheckIns;
    private List<PopularSessionResponse> popularSessions;
}
