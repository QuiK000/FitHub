package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.response.AttendanceResponse;
import com.dev.quikkkk.dto.response.AttendanceSessionResponse;

import java.util.List;

public interface IAttendanceService {
    List<AttendanceResponse> getMyAttendance();

    List<AttendanceSessionResponse> getAttendanceBySession(String sessionId);
}
