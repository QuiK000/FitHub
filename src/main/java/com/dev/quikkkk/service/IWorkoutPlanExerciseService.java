package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.AddExerciseToPlanRequest;
import com.dev.quikkkk.dto.request.ReorderWorkoutPlanExerciseRequest;
import com.dev.quikkkk.dto.request.UpdatePlanExerciseRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanExerciseResponse;

public interface IWorkoutPlanExerciseService {
    WorkoutPlanExerciseResponse addExerciseToPlan(String workoutPlanId, AddExerciseToPlanRequest request);

    WorkoutPlanExerciseResponse updatePlanExercise(String workoutPlanId, String exerciseId, UpdatePlanExerciseRequest request);

    MessageResponse deletePlanExercise(String workoutPlanId, String exerciseId, Integer dayNumber);

    MessageResponse reorderExercises(String workoutPlanId, ReorderWorkoutPlanExerciseRequest request);
}
