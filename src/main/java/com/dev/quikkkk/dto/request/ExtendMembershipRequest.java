package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtendMembershipRequest {
    @Positive(message = "VALIDATION.MEMBERSHIP.EXTEND.MONTHS.POSITIVE")
    private int months;
}
