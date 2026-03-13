package com.dev.quikkkk.modules.membership.service;

import com.dev.quikkkk.modules.membership.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.modules.membership.dto.request.ExtendMembershipRequest;
import com.dev.quikkkk.modules.membership.dto.response.MembershipHistoryResponse;
import com.dev.quikkkk.modules.membership.dto.response.MembershipResponse;
import com.dev.quikkkk.modules.membership.dto.response.MembershipValidationResponse;
import com.dev.quikkkk.core.dto.PageResponse;

public interface IMembershipService {
    MembershipResponse createMembership(CreateMembershipRequest request);

    MembershipResponse activateMembership(String membershipId);

    MembershipResponse freezeMembership(String membershipId);

    MembershipResponse unfreezeMembership(String membershipId);

    MembershipResponse extendMembership(String membershipId, ExtendMembershipRequest request);

    MembershipResponse cancelMembership(String membershipId);

    PageResponse<MembershipResponse> getMembershipByClientId(int page, int size, String clientId);

    MembershipResponse getMembershipByClientIdAndActive();

    MembershipHistoryResponse getMembershipClientHistory();

    MembershipValidationResponse validateMembership(String clientId);
}
