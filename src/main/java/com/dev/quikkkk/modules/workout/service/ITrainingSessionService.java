package com.dev.quikkkk.modules.workout.service;

import com.dev.quikkkk.modules.workout.dto.request.CheckInTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.dto.request.UpdateTrainingSessionRequest;
import com.dev.quikkkk.modules.workout.dto.response.CheckInResponse;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.workout.dto.response.TrainingSessionResponse;

public interface ITrainingSessionService {
    TrainingSessionResponse createSession(CreateTrainingSessionRequest request);

    PageResponse<TrainingSessionResponse> getTrainingSessions(int page, int size, String search);

    TrainingSessionResponse updateSession(String sessionId, UpdateTrainingSessionRequest request);

    MessageResponse joinToSession(String sessionId);

    CheckInResponse checkIn(String sessionId, CheckInTrainingSessionRequest request);
}
