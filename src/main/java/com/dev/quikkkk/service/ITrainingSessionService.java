package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CheckInTrainingSessionRequest;
import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.request.UpdateTrainingSessionRequest;
import com.dev.quikkkk.dto.response.CheckInResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.TrainingSessionResponse;

public interface ITrainingSessionService {
    TrainingSessionResponse createSession(CreateTrainingSessionRequest request);

    PageResponse<TrainingSessionResponse> getTrainingSessions(int page, int size, String search);

    TrainingSessionResponse updateSession(String sessionId, UpdateTrainingSessionRequest request);

    MessageResponse joinToSession(String sessionId);

    CheckInResponse checkIn(String sessionId, CheckInTrainingSessionRequest request);
}
