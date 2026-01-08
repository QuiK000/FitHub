package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.dto.response.TrainerProfileResponse;
import com.dev.quikkkk.entity.Specialization;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainerProfileMapper {
    public TrainerProfile toEntity(CreateTrainerProfileRequest request, User user, Set<Specialization> specializations) {
        return TrainerProfile.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .specialization(specializations)
                .experienceYears(request.getExperienceYears())
                .description(request.getDescription())
                .user(user)
                .active(true)
                .createdBy("SYSTEM")
                .build();
    }

    public TrainerProfileResponse toResponse(TrainerProfile profile) {
        return TrainerProfileResponse.builder()
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .specialization(profile.getSpecialization()
                        .stream()
                        .map(Specialization::getName)
                        .collect(Collectors.toSet()))
                .experienceYears(profile.getExperienceYears())
                .description(profile.getDescription())
                .createdAt(profile.getCreatedDate())
                .build();
    }
}
