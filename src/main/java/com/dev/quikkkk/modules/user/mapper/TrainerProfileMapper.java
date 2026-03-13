package com.dev.quikkkk.modules.user.mapper;

import com.dev.quikkkk.modules.user.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.modules.user.dto.request.UpdateTrainerProfileRequest;
import com.dev.quikkkk.modules.user.dto.response.TrainerProfileResponse;
import com.dev.quikkkk.modules.user.entity.Specialization;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.user.entity.User;
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
                .id(profile.getId())
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .specializations(profile.getSpecialization()
                        .stream()
                        .map(Specialization::getName)
                        .collect(Collectors.toSet()))
                .experienceYears(profile.getExperienceYears())
                .description(profile.getDescription())
                .createdAt(profile.getCreatedDate())
                .build();
    }

    public void update(TrainerProfile profile, UpdateTrainerProfileRequest request, Set<Specialization> specializations) {
        if (request.getFirstname() != null) profile.setFirstname(request.getFirstname());
        if (request.getLastname() != null) profile.setLastname(request.getLastname());
        if (request.getDescription() != null) profile.setDescription(request.getDescription());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (specializations != null)  {
            profile.getSpecialization().clear();
            profile.getSpecialization().addAll(specializations);
        }
    }
}
