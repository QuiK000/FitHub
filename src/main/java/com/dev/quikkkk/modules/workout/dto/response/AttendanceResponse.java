package com.dev.quikkkk.modules.workout.dto.response;

import com.dev.quikkkk.dto.response.SessionShortResponse;
import com.dev.quikkkk.modules.user.dto.response.TrainerShortResponse;
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
public class AttendanceResponse {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;
    private SessionShortResponse session;
    private TrainerShortResponse trainer;
}
