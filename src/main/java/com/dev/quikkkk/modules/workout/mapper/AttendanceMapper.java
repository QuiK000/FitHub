package com.dev.quikkkk.modules.workout.mapper;

import com.dev.quikkkk.modules.workout.dto.response.AttendanceResponse;
import com.dev.quikkkk.modules.workout.dto.response.AttendanceSessionResponse;
import com.dev.quikkkk.modules.user.dto.response.ClientShortResponse;
import com.dev.quikkkk.modules.workout.dto.response.SessionShortResponse;
import com.dev.quikkkk.modules.user.dto.response.TrainerShortResponse;
import com.dev.quikkkk.modules.workout.entity.Attendance;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.TrainerProfile;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
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
