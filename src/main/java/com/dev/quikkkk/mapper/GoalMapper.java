package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
import com.dev.quikkkk.dto.request.UpdateGoalProgressRequest;
import com.dev.quikkkk.dto.request.UpdateGoalRequest;
import com.dev.quikkkk.dto.response.GoalResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Goal;
import com.dev.quikkkk.enums.GoalStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GoalMapper {
    public Goal toEntity(CreateGoalRequest request, ClientProfile client) {
        return Goal.builder()
                .client(client)
                .title(request.getTitle())
                .description(request.getDescription())
                .goalType(request.getGoalType())
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
                .unit(goal.getUnit())
                .startDate(goal.getStartDate())
                .targetDate(goal.getTargetDate())
                .completionDate(goal.getCompletionDate())
                .status(goal.getStatus())
                .progressPercentage(goal.getProgressPercentage())
                .notes(goal.getNotes())
                .daysRemaining(0)
                .averageProgressPerDay(0.0)
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
        goal.setLastModifiedBy(goal.getClient().getId());
    }

    public void updateProgress(Goal goal, UpdateGoalProgressRequest request) {
        if (request.getCurrentValue() != null) goal.setCurrentValue(request.getCurrentValue());
        if (request.getNotes() != null) goal.setNotes(request.getNotes());
        goal.setLastModifiedBy(goal.getClient().getId());
    }
}
