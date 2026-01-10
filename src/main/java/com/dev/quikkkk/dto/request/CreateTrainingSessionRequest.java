package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainingSessionRequest {
    @NotNull(message = "VALIDATION.TRAINING.TYPE.NOT_NULL")
    private TrainingType type;

    @NotNull(message = "VALIDATION.TRAINING.START_TIME.NOT_NULL")
    @Future(message = "VALIDATION.TRAINING.START_TIME.FUTURE")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "VALIDATION.TRAINING.END_TIME.NOT_NULL")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Min(value = 1, message = "VALIDATION.TRAINING.MAX_PARTICIPANTS.MIN_1")
    private Integer maxParticipants;
}
