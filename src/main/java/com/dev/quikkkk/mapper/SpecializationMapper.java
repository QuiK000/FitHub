package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateSpecializationRequest;
import com.dev.quikkkk.dto.request.UpdateSpecializationRequest;
import com.dev.quikkkk.dto.response.SpecializationResponse;
import com.dev.quikkkk.entity.Specialization;
import org.springframework.stereotype.Service;

@Service
public class SpecializationMapper {
    public Specialization toEntity(CreateSpecializationRequest request) {
        return Specialization.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();
    }

    public SpecializationResponse toResponse(Specialization specialization) {
        return SpecializationResponse.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .description(specialization.getDescription())
                .build();
    }

    public void update(Specialization specialization, UpdateSpecializationRequest request) {
        if (request.getName() != null) specialization.setName(request.getName());
        if (request.getDescription() != null) specialization.setDescription(request.getDescription());
    }
}
