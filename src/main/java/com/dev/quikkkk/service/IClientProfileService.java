package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.dto.request.UpdateClientProfileRequest;
import com.dev.quikkkk.dto.response.ClientProfileResponse;
import com.dev.quikkkk.dto.response.MessageResponse;

public interface IClientProfileService {
    ClientProfileResponse createClientProfile(CreateClientProfileRequest request);

    ClientProfileResponse getClientProfile();

    MessageResponse updateClientProfile(UpdateClientProfileRequest request);

    MessageResponse deactivateProfile();

    ClientProfileResponse clearProfile();
}
