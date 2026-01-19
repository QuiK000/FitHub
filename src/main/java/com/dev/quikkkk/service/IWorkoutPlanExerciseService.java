package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.AddExerciseToPlanRequest;
import com.dev.quikkkk.dto.response.WorkoutPlanExerciseResponse;

public interface IWorkoutPlanExerciseService {
    WorkoutPlanExerciseResponse addExerciseToPlan(String workoutPlanId, AddExerciseToPlanRequest request);
}
