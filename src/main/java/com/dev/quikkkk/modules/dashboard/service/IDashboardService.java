package com.dev.quikkkk.modules.dashboard.service;

import com.dev.quikkkk.modules.workout.dto.response.AttendanceStatsResponse;
import com.dev.quikkkk.modules.user.dto.response.ClientAnalyticsResponse;
import com.dev.quikkkk.modules.dashboard.dto.DashboardAnalyticsResponse;
import com.dev.quikkkk.modules.membership.dto.response.RevenueStatsResponse;
import com.dev.quikkkk.modules.user.dto.response.TrainerAnalyticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface IDashboardService {
    DashboardAnalyticsResponse getDashboardAnalytics();

    TrainerAnalyticsResponse trainerAnalyticsById(String trainerId);

    TrainerAnalyticsResponse trainerAnalytics();

    ClientAnalyticsResponse clientAnalyticsById(String clientId);

    List<AttendanceStatsResponse> getAttendanceStats(LocalDate from, LocalDate to);

    List<RevenueStatsResponse> getRevenueStats(LocalDate from, LocalDate to);
}
