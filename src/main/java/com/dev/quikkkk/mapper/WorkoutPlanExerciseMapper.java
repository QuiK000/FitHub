package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.AddExerciseToPlanRequest;
import com.dev.quikkkk.dto.request.UpdatePlanExerciseRequest;
import com.dev.quikkkk.dto.response.ExerciseShortResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanExerciseResponse;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlanExercise;
import org.springframework.stereotype.Service;

@Service
public class WorkoutPlanExerciseMapper {
    public WorkoutPlanExercise toEntity(AddExerciseToPlanRequest request, Exercise exercise, WorkoutPlan plan) {
        return WorkoutPlanExercise.builder()
                .exercise(exercise)
                .dayNumber(request.getDayNumber())
                .orderIndex(request.getOrderIndex())
                .sets(request.getSets() != null ? request.getSets() : 3)
                .reps(request.getReps() != null ? request.getReps() : 12)
                .durationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 0)
                .restSeconds(request.getRestSeconds() != null ? request.getRestSeconds() : 60)
                .notes(request.getNotes())
                .createdBy(plan.getTrainer().getId())
                .workoutPlan(plan)
                .build();
    }

    public WorkoutPlanExerciseResponse toResponse(WorkoutPlanExercise workoutPlanExercise) {
        return WorkoutPlanExerciseResponse.builder()
                .id(workoutPlanExercise.getId())
                .exercise(
                        ExerciseShortResponse.builder()
                                .exerciseId(workoutPlanExercise.getExercise().getId())
                                .name(workoutPlanExercise.getExercise().getName())
                                .category(workoutPlanExercise.getExercise().getCategory())
                                .primaryMuscleGroup(workoutPlanExercise.getExercise().getPrimaryMuscleGroup())
                                .imageUrl(workoutPlanExercise.getExercise().getImageUrl())
                                .build()
                )
                .dayNumber(workoutPlanExercise.getDayNumber())
                .orderIndex(workoutPlanExercise.getOrderIndex())
                .sets(workoutPlanExercise.getSets())
                .reps(workoutPlanExercise.getReps())
                .durationSeconds(workoutPlanExercise.getDurationSeconds())
                .restSeconds(workoutPlanExercise.getRestSeconds())
                .notes(workoutPlanExercise.getNotes())
                .build();
    }

    public void update(UpdatePlanExerciseRequest request, WorkoutPlanExercise workoutPlanExercise, String userId) {
        if (request.getSets() != null) workoutPlanExercise.setSets(request.getSets());
        if (request.getReps() != null) workoutPlanExercise.setReps(request.getReps());
        if (request.getDurationSeconds() != null) workoutPlanExercise.setDurationSeconds(request.getDurationSeconds());
        if (request.getRestSeconds() != null) workoutPlanExercise.setRestSeconds(request.getRestSeconds());
        if (request.getNotes() != null) workoutPlanExercise.setNotes(request.getNotes());
        workoutPlanExercise.setLastModifiedBy(userId);
    }
}
