package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.response.TrainerShortResponse;
import com.dev.quikkkk.dto.response.TrainingSessionResponse;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.TrainingSession;
import com.dev.quikkkk.enums.TrainingStatus;
import org.springframework.stereotype.Service;

@Service
public class TrainingSessionMapper {
    public TrainingSession toEntity(CreateTrainingSessionRequest request, TrainerProfile trainer) {
        return TrainingSession.builder()
                .type(request.getType())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxParticipants(request.getMaxParticipants())
                .status(TrainingStatus.SCHEDULED)
                .trainer(trainer)
                .createdBy(trainer.getId())
                .build();
    }

    public TrainingSessionResponse toResponse(TrainingSession session) {
        return TrainingSessionResponse.builder()
                .id(session.getId())
                .type(session.getType())
                .status(session.getStatus())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .maxParticipants(session.getMaxParticipants())
                .currentParticipants(session.getClients() == null ? 0 :  session.getClients().size())
                .trainer(
                        TrainerShortResponse.builder()
                                .id(session.getTrainer().getId())
                                .firstname(session.getTrainer().getFirstname())
                                .lastname(session.getTrainer().getLastname())
                                .build()
                )
                .build();
    }
}
