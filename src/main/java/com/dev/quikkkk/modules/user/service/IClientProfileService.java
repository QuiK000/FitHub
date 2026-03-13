package com.dev.quikkkk.modules.user.service;

import com.dev.quikkkk.modules.user.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.modules.user.dto.request.UpdateClientProfileRequest;
import com.dev.quikkkk.modules.user.dto.response.ClientProfileResponse;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.core.dto.PageResponse;

public interface IClientProfileService {
    ClientProfileResponse createClientProfile(CreateClientProfileRequest request);

    ClientProfileResponse getClientProfile();

    PageResponse<ClientProfileResponse> getAllClientsProfile(int page, int size, String search);

    MessageResponse updateClientProfile(UpdateClientProfileRequest request);

    MessageResponse deactivateProfile();

    ClientProfileResponse clearProfile();
}
