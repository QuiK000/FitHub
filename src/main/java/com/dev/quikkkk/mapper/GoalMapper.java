package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.request.UpdateGoalRequest;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Goal;
import com.dev.quikkkk.enums.GoalStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class GoalMapper {
    public Goal toEntity(CreateGoalRequest request, ClientProfile client) {
        return Goal.builder()
                .client(client)
                .title(request.getTitle())
                .description(request.getDescription())
                .goalType(request.getGoalType())
                .startValue(request.getStartValue() != null ? request.getStartValue() : request.getCurrentValue())
                .targetValue(request.getTargetValue())
                .currentValue(request.getCurrentValue())
                .unit(request.getUnit())
                .startDate(LocalDateTime.now())
                .targetDate(request.getTargetDate())
                .status(GoalStatus.ACTIVE)
                .notes(request.getNotes())
                .createdBy(client.getId())
                .build();
    }

    public GoalResponse toResponse(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .goalType(goal.getGoalType())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .startValue(goal.getStartValue())
                .unit(goal.getUnit())
                .startDate(goal.getStartDate())
                .targetDate(goal.getTargetDate())
                .completionDate(goal.getCompletionDate())
                .status(goal.getStatus())
                .progressPercentage(goal.getProgressPercentage())
                .notes(goal.getNotes())
                .daysRemaining(calculateDaysRemaining(goal))
                .averageProgressPerDay(calculateAverageProgress(goal))
                .build();
    }

    public void update(Goal goal, UpdateGoalRequest request) {
        if (request.getTitle() != null) goal.setTitle(request.getTitle());
        if (request.getDescription() != null) goal.setDescription(request.getDescription());
        if (request.getGoalType() != null) goal.setGoalType(request.getGoalType());
        if (request.getTargetValue() != null) goal.setTargetValue(request.getTargetValue());
        if (request.getCurrentValue() != null) goal.setCurrentValue(request.getCurrentValue());
        if (request.getUnit() != null) goal.setUnit(request.getUnit());
        if (request.getTargetDate() != null) goal.setTargetDate(request.getTargetDate());
        if (request.getNotes() != null) goal.setNotes(request.getNotes());
        if (request.getStartValue() != null) goal.setStartValue(request.getStartValue());
        goal.setLastModifiedBy(goal.getClient().getId());
    }

    private Integer calculateDaysRemaining(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED || goal.getTargetDate() == null) return 0;
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), goal.getTargetDate());
        return Math.max(0, (int) days);
    }

    private Double calculateAverageProgress(Goal goal) {
        if (goal.getStartValue() == null || goal.getCurrentValue() == null || goal.getStartDate() == null) return 0.0;
        LocalDateTime calculationEndDate = (goal.getStatus() == GoalStatus.COMPLETED && goal.getCompletionDate() != null)
                ? goal.getCompletionDate()
                : LocalDateTime.now();

        long daysPassed = ChronoUnit.DAYS.between(goal.getStartDate(), calculationEndDate);
        double valueDifference = goal.getCurrentValue() - goal.getStartValue();
        double average;

        if (daysPassed <= 0) average = valueDifference;
        else average = valueDifference / daysPassed;

        return BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
