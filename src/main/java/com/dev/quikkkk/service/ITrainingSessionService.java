package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.response.TrainingSessionResponse;

public interface ITrainingSessionService {
    TrainingSessionResponse createSession(CreateTrainingSessionRequest request);
}
