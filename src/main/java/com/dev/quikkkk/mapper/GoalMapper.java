package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateGoalRequest;
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
}
