package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.MembershipType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMembershipRequest {
    @NotNull(message = "VALIDATION.MEMBERSHIP.CLIENT_ID.NOT.NULL")
    private String clientId;

    @NotNull(message = "VALIDATION.MEMBERSHIP.TYPE.NOT_NULL")
    private MembershipType type;

    @Positive(message = "VALIDATION.MEMBERSHIP.DURATION.MONTHS.POSITIVE")
    private Integer durationMonths;

    @PositiveOrZero(message = "VALIDATION.MEMBERSHIP.VISITS_LEMIT.NON_NEGATIVE")
    private Integer visitsLimit;
}
