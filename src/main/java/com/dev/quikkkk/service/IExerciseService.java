package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateExerciseRequest;
import com.dev.quikkkk.dto.request.UpdateExerciseRequest;
import com.dev.quikkkk.dto.response.ExerciseResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;

public interface IExerciseService {
    ExerciseResponse createExercise(CreateExerciseRequest request);

    ExerciseResponse findExerciseById(String exerciseId);

    PageResponse<ExerciseResponse> findAllExercises(int page, int size, String search);

    PageResponse<ExerciseResponse> findAllActiveExercises(int page, int size);

    PageResponse<ExerciseResponse> findAllExercisesByCategory(ExerciseCategory category, int page, int size);

    PageResponse<ExerciseResponse> findAllExercisesByMuscleGroup(MuscleGroup muscleGroup, int page, int size);

    ExerciseResponse updateExercise(String exerciseId, UpdateExerciseRequest request);

    MessageResponse activateExercise(String exerciseId);

    MessageResponse deactivateExercise(String exerciseId);
}
