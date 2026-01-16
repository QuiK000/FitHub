package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.response.AttendanceStatsResponse;
import com.dev.quikkkk.dto.response.ClientAnalyticsResponse;
import com.dev.quikkkk.dto.response.DashboardAnalyticsResponse;
import com.dev.quikkkk.dto.response.TrainerAnalyticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface IDashboardService {
    DashboardAnalyticsResponse getDashboardAnalytics();

    TrainerAnalyticsResponse trainerAnalyticsById(String trainerId);

    TrainerAnalyticsResponse trainerAnalytics();

    ClientAnalyticsResponse clientAnalyticsById(String clientId);

    List<AttendanceStatsResponse> getAttendanceStats(LocalDate from, LocalDate to);
}
