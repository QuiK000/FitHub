package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.dto.request.UpdateClientProfileRequest;
import com.dev.quikkkk.dto.response.ClientProfileResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.User;
import org.springframework.stereotype.Service;

@Service
public class ClientProfileMapper {
    public ClientProfile toEntity(CreateClientProfileRequest request, User user) {
        return ClientProfile.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .phone(request.getPhone())
                .birthdate(request.getBirthdate())
                .height(request.getHeight())
                .weight(request.getWeight())
                .user(user)
                .createdBy("SYSTEM")
                .active(true)
                .build();
    }

    public ClientProfileResponse toResponse(ClientProfile profile) {
        return ClientProfileResponse.builder()
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .phone(profile.getPhone())
                .birthdate(profile.getBirthdate())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .active(profile.isActive())
                .createdAt(profile.getCreatedDate())
                .build();
    }

    public void update(ClientProfile profile, UpdateClientProfileRequest request) {
        if (request.getFirstname() != null) profile.setFirstname(request.getFirstname());
        if (request.getLastname() != null) profile.setLastname(request.getLastname());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getBirthdate() != null) profile.setBirthdate(request.getBirthdate());
        if (request.getHeight() != null) profile.setHeight(request.getHeight());
        if (request.getWeight() != null) profile.setWeight(request.getWeight());
    }
}
