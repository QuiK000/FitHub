package com.dev.quikkkk.modules.membership.mapper;

import com.dev.quikkkk.modules.membership.dto.response.MembershipHistoryResponse;
import com.dev.quikkkk.modules.membership.dto.response.MembershipResponse;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.membership.entity.Membership;
import com.dev.quikkkk.modules.membership.enums.MembershipStatus;
import com.dev.quikkkk.modules.membership.enums.MembershipType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public MembershipHistoryResponse toHistoryResponse(List<Membership> memberships) {
        return MembershipHistoryResponse.builder()
                .memberships(
                        memberships.stream()
                                .map(this::toResponse)
                                .toList()
                )
                .build();
    }
}
