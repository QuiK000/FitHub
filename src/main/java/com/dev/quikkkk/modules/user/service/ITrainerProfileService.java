package com.dev.quikkkk.modules.user.service;

import com.dev.quikkkk.modules.user.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.modules.user.dto.request.UpdateTrainerProfileRequest;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.user.dto.response.TrainerProfileResponse;

public interface ITrainerProfileService {
    TrainerProfileResponse createTrainerProfile(CreateTrainerProfileRequest request);

    TrainerProfileResponse getTrainerProfile();

    PageResponse<TrainerProfileResponse> findAllTrainerProfiles(int page, int size, String search);

    TrainerProfileResponse getTrainerById(String id);

    TrainerProfileResponse updateTrainerProfile(UpdateTrainerProfileRequest request);

    MessageResponse deactivateProfile();

    TrainerProfileResponse clearProfile();
}
