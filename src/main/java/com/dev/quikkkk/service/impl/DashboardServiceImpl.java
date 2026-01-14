package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.DashboardAnalyticsResponse;
import com.dev.quikkkk.dto.response.PopularSessionResponse;
import com.dev.quikkkk.mapper.DashboardMapper;
import com.dev.quikkkk.repository.IAttendanceRepository;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.repository.IPaymentRepository;
import com.dev.quikkkk.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements IDashboardService {
    private final DashboardMapper dashboardMapper;
    private final IClientProfileRepository clientProfileRepository;
    private final IMembershipRepository membershipRepository;
    private final IPaymentRepository paymentRepository;
    private final IAttendanceRepository attendanceRepository;

    @Override
    public DashboardAnalyticsResponse getDashboardAnalytics() {
        Integer activeClients = clientProfileRepository.findAllActiveClients();
        Integer activeMemberships = membershipRepository.findAllActiveMemberships();
        BigDecimal revenue = paymentRepository.findPaymentsWhereStatusPaid();
        if (revenue == null) revenue = BigDecimal.ZERO;

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Integer todayCheckIns = attendanceRepository.countAttendanceByToday(start, end);
        Set<PopularSessionResponse> popularSessions = new HashSet<>(attendanceRepository
                .findTopSessions(PageRequest.of(0, 5))
                .getContent()
        );

        return dashboardMapper.toResponse(activeClients, activeMemberships, revenue, todayCheckIns, popularSessions);
    }
}
