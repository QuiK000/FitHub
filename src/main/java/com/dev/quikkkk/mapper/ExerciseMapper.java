package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateExerciseRequest;
import com.dev.quikkkk.dto.request.UpdateExerciseRequest;
import com.dev.quikkkk.dto.response.ExerciseResponse;
import com.dev.quikkkk.entity.Exercise;
import org.springframework.stereotype.Service;

@Service
public class ExerciseMapper {
    public Exercise toEntity(CreateExerciseRequest request, String userId) {
        return Exercise.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .primaryMuscleGroup(request.getPrimaryMuscleGroup())
                .secondaryMuscleGroups(request.getSecondaryMuscleGroups())
                .videoUrl(request.getVideoUrl())
                .imageUrl(request.getImageUrl())
                .instructions(request.getInstructions())
                .createdBy(userId)
                .active(true)
                .build();
    }

    public ExerciseResponse toResponse(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .category(exercise.getCategory())
                .primaryMuscleGroup(exercise.getPrimaryMuscleGroup())
                .secondaryMuscleGroups(exercise.getSecondaryMuscleGroups())
                .videoUrl(exercise.getVideoUrl())
                .imageUrl(exercise.getImageUrl())
                .instructions(exercise.getInstructions())
                .active(exercise.isActive())
                .createdAt(exercise.getCreatedDate())
                .build();
    }

    public void update(Exercise exercise, UpdateExerciseRequest request, String userId) {
        if (request.getName() != null) exercise.setName(request.getName());
        if (request.getDescription() != null) exercise.setDescription(request.getDescription());
        if (request.getCategory() != null) exercise.setCategory(request.getCategory());
        if (request.getPrimaryMuscleGroup() != null) exercise.setPrimaryMuscleGroup(request.getPrimaryMuscleGroup());
        if (request.getSecondaryMuscleGroups() != null)
            exercise.setSecondaryMuscleGroups(request.getSecondaryMuscleGroups());
        if (request.getVideoUrl() != null) exercise.setVideoUrl(request.getVideoUrl());
        if (request.getImageUrl() != null) exercise.setImageUrl(request.getImageUrl());
        if (request.getInstructions() != null) exercise.setInstructions(request.getInstructions());
        exercise.setLastModifiedBy(userId);
    }
}
