package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.WorkoutExerciseDetailsRequest;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlanExercise;
import org.springframework.stereotype.Service;

@Service
public class WorkoutPlanExerciseMapper {
    public WorkoutPlanExercise toEntity(
            Exercise exercise,
            WorkoutExerciseDetailsRequest workoutExerciseDetailsRequest,
            WorkoutPlan plan) {
        return WorkoutPlanExercise.builder()
                .workoutPlan(plan)
                .exercise(exercise)
                .dayNumber(workoutExerciseDetailsRequest.getDayNumber())
                .orderIndex(workoutExerciseDetailsRequest.getOrderIndex())
                .sets(workoutExerciseDetailsRequest.getSets() != null ? workoutExerciseDetailsRequest.getSets() : 3)
                .reps(workoutExerciseDetailsRequest.getReps() != null ? workoutExerciseDetailsRequest.getReps() : 12)
                .durationSeconds(workoutExerciseDetailsRequest.getDurationSeconds() != null
                        ? workoutExerciseDetailsRequest.getDurationSeconds()
                        : 0
                )
                .restSeconds(workoutExerciseDetailsRequest.getRestSeconds() != null
                        ? workoutExerciseDetailsRequest.getRestSeconds()
                        : 60
                )
                .notes(workoutExerciseDetailsRequest.getNotes())
                .createdBy(plan.getTrainer().getId())
                .build();
    }
}
