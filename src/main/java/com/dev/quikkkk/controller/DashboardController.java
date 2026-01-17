package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.response.AttendanceStatsResponse;
import com.dev.quikkkk.dto.response.ClientAnalyticsResponse;
import com.dev.quikkkk.dto.response.DashboardAnalyticsResponse;
import com.dev.quikkkk.dto.response.TrainerAnalyticsResponse;
import com.dev.quikkkk.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class DashboardController {
    private final IDashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAnalyticsResponse> getDashboardAnalytics() {
        return ResponseEntity.ok(dashboardService.getDashboardAnalytics());
    }

    @GetMapping("/trainers/{trainer-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerAnalyticsResponse> getTrainerAnalytics(@PathVariable("trainer-id") String trainerId) {
        return ResponseEntity.ok(dashboardService.trainerAnalyticsById(trainerId));
    }

    @GetMapping("/clients/{client-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientAnalyticsResponse> getClientsAnalytics(@PathVariable("client-id") String clientId) {
        return ResponseEntity.ok(dashboardService.clientAnalyticsById(clientId));
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceStatsResponse>> getAttendanceStats(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(dashboardService.getAttendanceStats(from, to));
    }

    @GetMapping("/trainers/me")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerAnalyticsResponse> getTrainerAnalyticsByClient() {
        return ResponseEntity.ok(dashboardService.trainerAnalytics());
    }
}
