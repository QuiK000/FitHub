package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.LogWaterIntakeRequest;
import com.dev.quikkkk.dto.response.DailyWaterIntakeResponse;
import com.dev.quikkkk.dto.response.WaterIntakeResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.WaterIntake;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaterIntakeMapper {
    public WaterIntake toEntity(LogWaterIntakeRequest request, ClientProfile client, Integer dailyTarget, String userId) {
        return WaterIntake.builder()
                .client(client)
                .amountMl(request.getAmountMl())
                .targetMl(dailyTarget)
                .intakeDate(LocalDate.now())
                .intakeTime(LocalDateTime.now())
                .createdBy(userId)
                .build();
    }

    public WaterIntakeResponse toResponse(WaterIntake intake, Double progress) {
        return WaterIntakeResponse.builder()
                .id(intake.getId())
                .intakeDate(intake.getIntakeDate())
                .amountMl(intake.getAmountMl())
                .targetMl(intake.getTargetMl())
                .intakeTime(intake.getIntakeTime())
                .progress(progress)
                .build();
    }

    public DailyWaterIntakeResponse toResponse(
            LocalDate date,
            int currentTotal,
            int target,
            double progress,
            List<WaterIntakeResponse> intakeDto
    ) {
        return DailyWaterIntakeResponse.builder()
                .date(date)
                .totalMl(currentTotal)
                .targetMl(target)
                .progress(progress)
                .intakes(intakeDto)
                .build();
    }
}
