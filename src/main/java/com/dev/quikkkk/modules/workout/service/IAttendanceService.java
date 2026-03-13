package com.dev.quikkkk.modules.workout.service;

import com.dev.quikkkk.modules.workout.dto.response.AttendanceResponse;
import com.dev.quikkkk.modules.workout.dto.response.AttendanceSessionResponse;

import java.util.List;

public interface IAttendanceService {
    List<AttendanceResponse> getMyAttendance();

    List<AttendanceSessionResponse> getAttendanceBySession(String sessionId);
}
