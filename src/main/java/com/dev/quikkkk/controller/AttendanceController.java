package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.response.AttendanceResponse;
import com.dev.quikkkk.dto.response.AttendanceSessionResponse;
import com.dev.quikkkk.service.IAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final IAttendanceService attendanceService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance() {
        return ResponseEntity.ok(attendanceService.getMyAttendance());
    }

    @GetMapping("/session/{session-id}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<AttendanceSessionResponse>> getAttendanceBySession(@PathVariable("session-id") String sessionId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySession(sessionId));
    }
}
