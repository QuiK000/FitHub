package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.AttendanceResponse;
import com.dev.quikkkk.dto.response.AttendanceSessionResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.TrainingSession;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.AttendanceMapper;
import com.dev.quikkkk.repository.IAttendanceRepository;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.ITrainingSessionRepository;
import com.dev.quikkkk.service.IAttendanceService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.SESSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements IAttendanceService {
    private final IAttendanceRepository attendanceRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final ITrainingSessionRepository trainingSessionRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendance() {
        ClientProfile client = findClientProfileByUserId();
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

    private ClientProfile findClientProfileByUserId() {
        String userId = SecurityUtils.getCurrentUserId();
        return clientProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
    }
}
