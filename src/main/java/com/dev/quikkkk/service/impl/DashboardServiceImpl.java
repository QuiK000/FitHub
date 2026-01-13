package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.DashboardAnalyticsResponse;
import com.dev.quikkkk.mapper.DashboardMapper;
import com.dev.quikkkk.repository.IAttendanceRepository;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.repository.IPaymentRepository;
import com.dev.quikkkk.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Integer todayCheckIns = attendanceRepository.countAttendanceByToday(start, end);

        return null;
    }
}
