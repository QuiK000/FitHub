package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;

public interface IWorkoutLogService {
    WorkoutLogResponse createWorkoutLog(LogWorkoutRequest request);
}
