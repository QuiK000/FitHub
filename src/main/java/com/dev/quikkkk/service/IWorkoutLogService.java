package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.request.UpdateLogWorkoutRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;

public interface IWorkoutLogService {
    WorkoutLogResponse createWorkoutLog(LogWorkoutRequest request);

    PageResponse<WorkoutLogResponse> getAllWorkoutLogs(int page, int size);

    WorkoutLogResponse getWorkoutLogById(String id);

    WorkoutLogResponse updateWorkoutLogById(String id, UpdateLogWorkoutRequest request);

    PageResponse<WorkoutLogResponse> getMyWorkoutLogs(int page, int size);
}
