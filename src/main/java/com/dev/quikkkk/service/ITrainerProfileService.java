package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.dto.response.TrainerProfileResponse;

public interface ITrainerProfileService {
    TrainerProfileResponse createTrainerProfile(CreateTrainerProfileRequest request);
}
