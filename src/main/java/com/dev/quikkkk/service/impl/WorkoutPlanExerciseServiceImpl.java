package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.AddExerciseToPlanRequest;
import com.dev.quikkkk.dto.response.WorkoutPlanExerciseResponse;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlanExercise;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.WorkoutPlanExerciseMapper;
import com.dev.quikkkk.repository.IExerciseRepository;
import com.dev.quikkkk.repository.IWorkoutPlanExerciseRepository;
import com.dev.quikkkk.repository.IWorkoutPlanRepository;
import com.dev.quikkkk.service.IWorkoutPlanExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_ALREADY_IN_PLAN;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanExerciseServiceImpl implements IWorkoutPlanExerciseService {
    private final IWorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final IWorkoutPlanRepository workoutPlanRepository;
    private final IExerciseRepository exerciseRepository;
    private final WorkoutPlanExerciseMapper workoutPlanExerciseMapper;

    @Override
    @Transactional
    public WorkoutPlanExerciseResponse addExerciseToPlan(String workoutPlanId, AddExerciseToPlanRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new BusinessException(EXERCISE_NOT_FOUND));
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new BusinessException(WORKOUT_PLAN_NOT_FOUND));

        WorkoutPlanExercise workoutPlanExercise = workoutPlanExerciseMapper.toEntity(request, exercise, plan);

        if (workoutPlanExercise.getExercise().getId().equals(request.getExerciseId()))
            throw new BusinessException(EXERCISE_ALREADY_IN_PLAN);

        plan.getExercises().add(workoutPlanExercise);
        workoutPlanExerciseRepository.save(workoutPlanExercise);
        return workoutPlanExerciseMapper.toResponse(workoutPlanExercise);
    }
}
