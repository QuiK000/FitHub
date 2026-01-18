package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateWorkoutPlanRequest;
import com.dev.quikkkk.dto.request.WorkoutExerciseDetailsRequest;
import com.dev.quikkkk.dto.response.WorkoutPlanResponse;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlanExercise;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.WorkoutPlanExerciseMapper;
import com.dev.quikkkk.mapper.WorkoutPlanMapper;
import com.dev.quikkkk.repository.IExerciseRepository;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.repository.IWorkoutPlanRepository;
import com.dev.quikkkk.service.IWorkoutPlanService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static com.dev.quikkkk.enums.ErrorCode.DUPLICATE_EXERCISE_ORDER;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_DEACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.INVALID_WORKOUT_DAY;
import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanServiceImpl implements IWorkoutPlanService {
    private final IWorkoutPlanRepository workoutPlanRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final IExerciseRepository exerciseRepository;
    private final WorkoutPlanMapper workoutPlanMapper;
    private final WorkoutPlanExerciseMapper workoutPlanExerciseMapper;

    @Override
    @Transactional
    public WorkoutPlanResponse createWorkoutPlan(CreateWorkoutPlanRequest request) {
        TrainerProfile trainer = getTrainerProfile();

        WorkoutPlan plan = workoutPlanMapper.toEntity(request, trainer);
        validateExercises(request, plan);

        workoutPlanRepository.save(plan);
        return workoutPlanMapper.toResponse(plan);
    }

    private void validateExercises(CreateWorkoutPlanRequest request, WorkoutPlan plan) {
        var usedIndexesPerDay = new HashSet<String>();
        for (WorkoutExerciseDetailsRequest exerciseDetailsRequest : request.getExercises()) {
            Exercise exercise = exerciseRepository.findById(exerciseDetailsRequest.getExerciseId())
                    .orElseThrow(() -> new BusinessException(EXERCISE_NOT_FOUND));
            if (!exercise.isActive()) throw new BusinessException(EXERCISE_DEACTIVATED);
            if (exerciseDetailsRequest.getDayNumber() > plan.getSessionsPerWeek())
                throw new BusinessException(INVALID_WORKOUT_DAY);

            String indexKey = exerciseDetailsRequest.getDayNumber() + ":" + exerciseDetailsRequest.getOrderIndex();
            if (!usedIndexesPerDay.add(indexKey)) throw new BusinessException(DUPLICATE_EXERCISE_ORDER);

            WorkoutPlanExercise wpe = workoutPlanExerciseMapper.toEntity(exercise, exerciseDetailsRequest, plan);
            plan.getExercises().add(wpe);
        }
    }

    private TrainerProfile getTrainerProfile() {
        String userId = SecurityUtils.getCurrentUserId();
        return trainerProfileRepository.findTrainerProfileByUserId(userId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
    }
}
