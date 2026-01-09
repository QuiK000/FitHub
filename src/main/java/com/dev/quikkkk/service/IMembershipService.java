package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.response.MembershipResponse;

public interface IMembershipService {
    MembershipResponse createMembership(CreateMembershipRequest request);

    MembershipResponse activateMembership(String membershipId);
}
