package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.response.MembershipResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MembershipMapper {
    public Membership toEntity(
            MembershipType type,
            MembershipStatus status,
            LocalDateTime startDate,
            LocalDateTime endTime,
            Integer visitsLeft,
            Integer durationMonths,
            ClientProfile client
    ) {
        return Membership.builder()
                .type(type)
                .status(status)
                .startDate(startDate)
                .endDate(endTime)
                .visitsLeft(visitsLeft)
                .durationMonths(durationMonths)
                .client(client)
                .createdBy("ADMIN")
                .build();
    }

    public MembershipResponse toResponse(Membership membership) {
        return MembershipResponse.builder()
                .id(membership.getId())
                .type(membership.getType())
                .status(membership.getStatus())
                .startDate(membership.getStartDate())
                .endDate(membership.getEndDate())
                .visitsLeft(membership.getVisitsLeft())
                .build();
    }
}
