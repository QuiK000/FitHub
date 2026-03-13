package com.dev.quikkkk.modules.workout.service.impl;

import com.dev.quikkkk.modules.workout.dto.response.AttendanceResponse;
import com.dev.quikkkk.modules.workout.dto.response.AttendanceSessionResponse;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.workout.entity.TrainingSession;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.workout.mapper.AttendanceMapper;
import com.dev.quikkkk.modules.workout.repository.IAttendanceRepository;
import com.dev.quikkkk.modules.workout.repository.ITrainingSessionRepository;
import com.dev.quikkkk.modules.workout.service.IAttendanceService;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dev.quikkkk.core.enums.ErrorCode.SESSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements IAttendanceService {
    private final IAttendanceRepository attendanceRepository;
    private final ITrainingSessionRepository trainingSessionRepository;
    private final AttendanceMapper attendanceMapper;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendance() {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        return attendanceRepository.findAllByClientId(client.getId()).stream()
                .map(attendanceMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSessionResponse> getAttendanceBySession(String sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(SESSION_NOT_FOUND));

        return attendanceRepository.findAllBySessionId(session.getId()).stream()
                .map(attendanceMapper::toResponseForTrainer)
                .toList();
    }
}
