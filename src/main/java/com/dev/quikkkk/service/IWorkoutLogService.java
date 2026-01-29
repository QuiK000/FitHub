package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.request.UpdateLogWorkoutRequest;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;

import java.util.List;

public interface IWorkoutLogService {
    WorkoutLogResponse createWorkoutLog(LogWorkoutRequest request);

    List<WorkoutLogResponse> getAllWorkoutLogs();

    WorkoutLogResponse getWorkoutLogById(String id);

    WorkoutLogResponse updateWorkoutLogById(String id, UpdateLogWorkoutRequest request);

    List<WorkoutLogResponse> getMyWorkoutLogs();
}
