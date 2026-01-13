package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipShortResponse {
    private String membershipId;
    private MembershipType type;
    private MembershipStatus status;
}
