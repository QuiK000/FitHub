package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.AddExerciseToPlanRequest;
import com.dev.quikkkk.dto.request.ReorderWorkoutPlanExerciseItem;
import com.dev.quikkkk.dto.request.ReorderWorkoutPlanExerciseRequest;
import com.dev.quikkkk.dto.request.UpdatePlanExerciseRequest;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.WorkoutPlanExerciseResponse;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.WorkoutPlan;
import com.dev.quikkkk.entity.WorkoutPlanExercise;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MessageMapper;
import com.dev.quikkkk.mapper.WorkoutPlanExerciseMapper;
import com.dev.quikkkk.repository.IExerciseRepository;
import com.dev.quikkkk.repository.IWorkoutPlanExerciseRepository;
import com.dev.quikkkk.repository.IWorkoutPlanRepository;
import com.dev.quikkkk.service.IWorkoutPlanExerciseService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.dev.quikkkk.enums.ErrorCode.EXERCISE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.PLAN_EXERCISE_ALREADY_EXISTS;
import static com.dev.quikkkk.enums.ErrorCode.PLAN_EXERCISE_DAY_EMPTY;
import static com.dev.quikkkk.enums.ErrorCode.PLAN_EXERCISE_DAY_MISMATCH;
import static com.dev.quikkkk.enums.ErrorCode.PLAN_EXERCISE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.PLAN_EXERCISE_NOT_IN_PLAN;
import static com.dev.quikkkk.enums.ErrorCode.PLAN_EXERCISE_ORDER_DUPLICATED;
import static com.dev.quikkkk.enums.ErrorCode.WORKOUT_PLAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanExerciseServiceImpl implements IWorkoutPlanExerciseService {
    private final IWorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final IWorkoutPlanRepository workoutPlanRepository;
    private final IExerciseRepository exerciseRepository;
    private final WorkoutPlanExerciseMapper workoutPlanExerciseMapper;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public WorkoutPlanExerciseResponse addExerciseToPlan(String workoutPlanId, AddExerciseToPlanRequest request) {
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new BusinessException(EXERCISE_NOT_FOUND));
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new BusinessException(WORKOUT_PLAN_NOT_FOUND));

        boolean alreadyExists = plan.getExercises().stream()
                .anyMatch(e ->
                        e.getExercise().getId().equals(request.getExerciseId()) &&
                                e.getDayNumber().equals(request.getDayNumber())
                );

        if (alreadyExists) throw new BusinessException(PLAN_EXERCISE_ALREADY_EXISTS);
        WorkoutPlanExercise workoutPlanExercise = workoutPlanExerciseMapper.toEntity(request, exercise, plan);

        plan.getExercises().add(workoutPlanExercise);
        workoutPlanExerciseRepository.save(workoutPlanExercise);

        return workoutPlanExerciseMapper.toResponse(workoutPlanExercise);
    }

    @Override
    @Transactional
    public WorkoutPlanExerciseResponse updatePlanExercise(
            String workoutPlanId,
            String exerciseId,
            UpdatePlanExerciseRequest request
    ) {
        String userId = SecurityUtils.getCurrentUserId();
        WorkoutPlanExercise wpe = workoutPlanExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new BusinessException(PLAN_EXERCISE_NOT_FOUND));

        if (!wpe.getWorkoutPlan().getId().equals(workoutPlanId)) throw new BusinessException(PLAN_EXERCISE_NOT_IN_PLAN);

        workoutPlanExerciseMapper.update(request, wpe, userId);
        return workoutPlanExerciseMapper.toResponse(wpe);
    }

    @Override
    @Transactional
    public MessageResponse deletePlanExercise(String workoutPlanId, String exerciseId, Integer dayNumber) {
        WorkoutPlanExercise wpe = workoutPlanExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new BusinessException(PLAN_EXERCISE_NOT_FOUND));

        if (!wpe.getWorkoutPlan().getId().equals(workoutPlanId)) throw new BusinessException(PLAN_EXERCISE_NOT_IN_PLAN);
        if (dayNumber != null && !wpe.getDayNumber().equals(dayNumber))
            throw new BusinessException(PLAN_EXERCISE_DAY_MISMATCH);

        workoutPlanExerciseRepository.delete(wpe);
        return messageMapper.message("Exercise removed from day " + dayNumber);
    }

    @Override
    @Transactional
    public MessageResponse reorderExercises(String workoutPlanId, ReorderWorkoutPlanExerciseRequest request) {
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new BusinessException(WORKOUT_PLAN_NOT_FOUND));
        Integer day = request.getDay();
        var dayExercises = plan.getExercises().stream()
                .filter(e -> e.getDayNumber().equals(day))
                .toList();

        if (dayExercises.isEmpty()) throw new BusinessException(PLAN_EXERCISE_DAY_EMPTY);
        Map<String, WorkoutPlanExercise> exercisesById = dayExercises.stream()
                .collect(Collectors.toMap(
                        WorkoutPlanExercise::getId,
                        e -> e
                ));

        for (var item : request.getExercises()) {
            if (!exercisesById.containsKey(item.getPlanExerciseId())) {
                throw new BusinessException(PLAN_EXERCISE_NOT_IN_PLAN);
            }
        }

        Set<Integer> orderIndexes = request.getExercises().stream()
                .map(ReorderWorkoutPlanExerciseItem::getOrderIndex)
                .collect(Collectors.toSet());

        if (orderIndexes.size() != request.getExercises().size())
            throw new BusinessException(PLAN_EXERCISE_ORDER_DUPLICATED);

        if (request.getExercises().size() != dayExercises.size())
            throw new BusinessException(PLAN_EXERCISE_ORDER_DUPLICATED);

        for (var item : request.getExercises()) {
            WorkoutPlanExercise wpe = exercisesById.get(item.getPlanExerciseId());
            wpe.setOrderIndex(item.getOrderIndex());
        }

        normalizeOrderIndexes(dayExercises);
        return messageMapper.message("Exercise reordered successfully");
    }

    private void normalizeOrderIndexes(List<WorkoutPlanExercise> exercises) {
        exercises.stream()
                .sorted(Comparator.comparing(WorkoutPlanExercise::getOrderIndex))
                .forEachOrdered(new Consumer<>() {
                    private int index = 0;

                    @Override
                    public void accept(WorkoutPlanExercise e) {
                        e.setOrderIndex(index++);
                    }
                });
    }
}
