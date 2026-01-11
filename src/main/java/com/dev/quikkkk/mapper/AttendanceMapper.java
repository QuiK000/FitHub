package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.AttendanceResponse;
import com.dev.quikkkk.dto.response.AttendanceSessionResponse;
import com.dev.quikkkk.dto.response.ClientShortResponse;
import com.dev.quikkkk.dto.response.SessionShortResponse;
import com.dev.quikkkk.dto.response.TrainerShortResponse;
import com.dev.quikkkk.entity.Attendance;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.TrainingSession;
import org.springframework.stereotype.Service;

@Service
public class AttendanceMapper {
    public AttendanceResponse toResponse(Attendance attendance) {
        TrainingSession session = attendance.getSession();
        TrainerProfile trainer = session.getTrainer();

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .checkInTime(attendance.getCheckInTime())
                .session(
                        SessionShortResponse.builder()
                                .sessionId(session.getId())
                                .sessionStartTime(session.getStartTime())
                                .sessionEndTime(session.getEndTime())
                                .build()
                )
                .trainer(
                        TrainerShortResponse.builder()
                                .trainerId(trainer.getId())
                                .firstname(trainer.getFirstname())
                                .lastname(trainer.getLastname())
                                .build()
                )
                .build();
    }

    public AttendanceSessionResponse toResponseForTrainer(Attendance attendance) {
        ClientProfile client = attendance.getClient();

        return AttendanceSessionResponse.builder()
                .id(attendance.getId())
                .checkInTime(attendance.getCheckInTime())
                .client(
                        ClientShortResponse.builder()
                                .clientId(client.getId())
                                .clientFirstname(client.getFirstname())
                                .clientLastname(client.getLastname())
                                .build()
                )
                .build();
    }
}
