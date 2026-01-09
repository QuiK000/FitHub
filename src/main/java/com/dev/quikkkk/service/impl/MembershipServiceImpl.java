package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.response.MembershipResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MembershipMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.service.IMembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_ALREADY_ACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_DURATION_REQUIRED;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_VISITS_REQUIRED;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipServiceImpl implements IMembershipService {
    private final IMembershipRepository membershipRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final MembershipMapper membershipMapper;

    @Override
    public MembershipResponse createMembership(CreateMembershipRequest request) {
        ClientProfile client = clientProfileRepository.findById(request.getClientId())
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));

        validateCreateRequest(request);
        Membership membership;

        switch (request.getType()) {
            case VISITS -> membership = membershipMapper.toEntity(
                    request.getType(),
                    MembershipStatus.CREATED,
                    null,
                    null,
                    request.getVisitsLimit(),
                    request.getDurationMonths(),
                    client
            );
            case MONTHLY, YEARLY -> membership = membershipMapper.toEntity(
                    request.getType(),
                    MembershipStatus.CREATED,
                    null,
                    null,
                    null,
                    request.getDurationMonths(),
                    client
            );
            default -> throw new IllegalStateException("Unexpected value: " + request.getType());
        }

        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse activateMembership(String membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new BusinessException(MEMBERSHIP_NOT_FOUND));

        if (membership.getStatus() != MembershipStatus.CREATED)
            throw new BusinessException(MEMBERSHIP_ALREADY_ACTIVATED);

        boolean hasActive = membershipRepository.existsByClientIdAndStatus(
                membership.getClient().getId(),
                MembershipStatus.ACTIVE
        );

        if (hasActive) throw new BusinessException(MEMBERSHIP_ALREADY_ACTIVATED);

        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setStartDate(LocalDateTime.now());

        if (membership.getType() != MembershipType.VISITS) {
            membership.setEndDate(
                    membership.getStartDate().plusMonths(membership.getDurationMonths())
            );
        }

        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    private void validateCreateRequest(CreateMembershipRequest request) {
        switch (request.getType()) {
            case VISITS -> {
                if (request.getVisitsLimit() == null) {
                    throw new BusinessException(MEMBERSHIP_VISITS_REQUIRED);
                }
            }
            case MONTHLY, YEARLY -> {
                if (request.getDurationMonths() == null) {
                    throw new BusinessException(MEMBERSHIP_DURATION_REQUIRED);
                }
            }
        }
    }
}
