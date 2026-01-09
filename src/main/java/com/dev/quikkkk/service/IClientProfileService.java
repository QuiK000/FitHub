package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.dto.request.UpdateClientProfileRequest;
import com.dev.quikkkk.dto.response.ClientProfileResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;

public interface IClientProfileService {
    ClientProfileResponse createClientProfile(CreateClientProfileRequest request);

    ClientProfileResponse getClientProfile();

    PageResponse<ClientProfileResponse> getAllClientsProfile(int page, int size, String search);

    MessageResponse updateClientProfile(UpdateClientProfileRequest request);

    MessageResponse deactivateProfile();

    ClientProfileResponse clearProfile();
}
