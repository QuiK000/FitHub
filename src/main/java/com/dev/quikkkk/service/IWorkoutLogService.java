package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.LogWorkoutRequest;
import com.dev.quikkkk.dto.request.UpdateLogWorkoutRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.WorkoutLogResponse;

import java.time.LocalDate;

public interface IWorkoutLogService {
    WorkoutLogResponse createWorkoutLog(LogWorkoutRequest request);

    PageResponse<WorkoutLogResponse> getAllWorkoutLogs(int page, int size);

    WorkoutLogResponse getWorkoutLogById(String id);

    WorkoutLogResponse updateWorkoutLogById(String id, UpdateLogWorkoutRequest request);

    PageResponse<WorkoutLogResponse> getMyWorkoutLogs(int page, int size);

    PageResponse<WorkoutLogResponse> getLogsByAssignment(String assignmentId, int page, int size);

    PageResponse<WorkoutLogResponse> getLogsByExercise(String exerciseId, int page, int size);

    PageResponse<WorkoutLogResponse> getLogsByDateRange(LocalDate from, LocalDate to, int page, int size);
}
