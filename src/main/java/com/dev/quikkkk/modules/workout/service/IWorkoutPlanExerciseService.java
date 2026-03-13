package com.dev.quikkkk.modules.workout.service;

import com.dev.quikkkk.modules.workout.dto.request.AddExerciseToPlanRequest;
import com.dev.quikkkk.modules.workout.dto.request.ReorderWorkoutPlanExerciseRequest;
import com.dev.quikkkk.modules.workout.dto.request.UpdatePlanExerciseRequest;
import com.dev.quikkkk.core.dto.MessageResponse;
import com.dev.quikkkk.modules.workout.dto.response.WorkoutPlanExerciseResponse;

public interface IWorkoutPlanExerciseService {
    WorkoutPlanExerciseResponse addExerciseToPlan(String workoutPlanId, AddExerciseToPlanRequest request);

    WorkoutPlanExerciseResponse updatePlanExercise(String workoutPlanId, String exerciseId, UpdatePlanExerciseRequest request);

    MessageResponse deletePlanExercise(String workoutPlanId, String exerciseId, Integer dayNumber);

    MessageResponse reorderExercises(String workoutPlanId, ReorderWorkoutPlanExerciseRequest request);
}
