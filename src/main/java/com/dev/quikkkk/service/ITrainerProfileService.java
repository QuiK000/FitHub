package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.dto.request.UpdateTrainerProfileRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.TrainerProfileResponse;

public interface ITrainerProfileService {
    TrainerProfileResponse createTrainerProfile(CreateTrainerProfileRequest request);

    TrainerProfileResponse getTrainerProfile();

    PageResponse<TrainerProfileResponse> findAllTrainerProfiles(int page, int size, String search);

    TrainerProfileResponse getTrainerById(String id);

    TrainerProfileResponse updateTrainerProfile(UpdateTrainerProfileRequest request);

    MessageResponse deactivateProfile();

    TrainerProfileResponse clearProfile();
}
