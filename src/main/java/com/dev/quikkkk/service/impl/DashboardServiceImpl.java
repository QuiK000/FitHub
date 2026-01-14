package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.response.DashboardAnalyticsResponse;
import com.dev.quikkkk.dto.response.PopularSessionResponse;
import com.dev.quikkkk.dto.response.TrainerAnalyticsResponse;
import com.dev.quikkkk.dto.response.TrainerAttendanceMetrics;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.DashboardMapper;
import com.dev.quikkkk.repository.IAttendanceRepository;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.repository.IPaymentRepository;
import com.dev.quikkkk.repository.ITrainerProfileRepository;
import com.dev.quikkkk.repository.ITrainingSessionRepository;
import com.dev.quikkkk.service.IDashboardService;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.dev.quikkkk.enums.ErrorCode.TRAINER_PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements IDashboardService {
    private final DashboardMapper dashboardMapper;
    private final IClientProfileRepository clientProfileRepository;
    private final ITrainerProfileRepository trainerProfileRepository;
    private final ITrainingSessionRepository trainingSessionRepository;
    private final IMembershipRepository membershipRepository;
    private final IPaymentRepository paymentRepository;
    private final IAttendanceRepository attendanceRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardAnalyticsResponse getDashboardAnalytics() {
        Integer activeClients = clientProfileRepository.findAllActiveClients();
        Integer activeMemberships = membershipRepository.findAllActiveMemberships();
        BigDecimal revenue = paymentRepository.findPaymentsWhereStatusPaid();
        if (revenue == null) revenue = BigDecimal.ZERO;

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Integer todayCheckIns = attendanceRepository.countAttendanceByToday(start, end);
        List<PopularSessionResponse> popularSessions = attendanceRepository
                .findTopSessions(PageRequest.of(0, 5))
                .getContent();

        return dashboardMapper.dashboardToResponse(activeClients, activeMemberships, revenue, todayCheckIns, popularSessions);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerAnalyticsResponse trainerAnalyticsById(String trainerId) {
        TrainerProfile trainer = trainerProfileRepository.findTrainerProfileById(trainerId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
        TrainerAttendanceMetrics metrics = calculateTrainerAttendanceMetrics(trainer.getId());

        return dashboardMapper.trainerAnalyticsToResponse(trainer, metrics);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerAnalyticsResponse trainerAnalytics() {
        String userId = SecurityUtils.getCurrentUserId();
        TrainerProfile trainer = trainerProfileRepository.findTrainerProfileByUserId(userId)
                .orElseThrow(() -> new BusinessException(TRAINER_PROFILE_NOT_FOUND));
        TrainerAttendanceMetrics metrics = calculateTrainerAttendanceMetrics(trainer.getId());

        return dashboardMapper.trainerAnalyticsToResponse(trainer, metrics);
    }

    private TrainerAttendanceMetrics calculateTrainerAttendanceMetrics(String trainerId) {
        long totalSessions = trainingSessionRepository.countAllSessionsByTrainer(trainerId);
        long totalClients = attendanceRepository.countAllClientsByTrainer(trainerId);
        long totalAttendance = attendanceRepository.countAllAttendancesByTrainer(trainerId);
        double attendanceRate = totalSessions == 0 ? 0 : (double) totalAttendance / totalSessions;

        return TrainerAttendanceMetrics.builder()
                .totalSessions(totalSessions)
                .totalClients(totalClients)
                .attendanceRate(attendanceRate)
                .build();
    }
}
