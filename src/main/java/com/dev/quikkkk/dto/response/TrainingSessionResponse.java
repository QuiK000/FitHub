package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.TrainingStatus;
import com.dev.quikkkk.enums.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingSessionResponse {
    private String id;
    private TrainingType type;
    private TrainingStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private TrainerShortResponse trainer;
}
