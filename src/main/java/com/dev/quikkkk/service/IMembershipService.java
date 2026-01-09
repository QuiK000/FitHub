package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.request.ExtendMembershipRequest;
import com.dev.quikkkk.dto.response.MembershipHistoryResponse;
import com.dev.quikkkk.dto.response.MembershipResponse;
import com.dev.quikkkk.dto.response.MembershipValidationResponse;
import com.dev.quikkkk.dto.response.PageResponse;

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
