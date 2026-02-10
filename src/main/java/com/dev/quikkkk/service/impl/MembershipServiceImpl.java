package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.request.ExtendMembershipRequest;
import com.dev.quikkkk.dto.response.MembershipHistoryResponse;
import com.dev.quikkkk.dto.response.MembershipResponse;
import com.dev.quikkkk.dto.response.MembershipValidationResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.MembershipMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.service.IMembershipService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import com.dev.quikkkk.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_ALREADY_HAS_ACTIVE_MEMBERSHIP;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_DURATION_REQUIRED;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_END_DATE_MISSING;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_NOT_FROZEN;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_STATUS_NOT_ACTIVATABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_STATUS_NOT_CANCELLABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_STATUS_NOT_EXTENDABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_STATUS_NOT_FREEZABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_TYPE_NOT_EXTENDABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_TYPE_NOT_FREEZABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_VISITS_REQUIRED;
import static com.dev.quikkkk.enums.ErrorCode.NO_ACTIVE_MEMBERSHIP;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipServiceImpl implements IMembershipService {
    private final IMembershipRepository membershipRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final MembershipMapper membershipMapper;
    private final ClientProfileUtils clientProfileUtils;

    @Override
    @Transactional
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
        Membership membership = findMembershipById(membershipId);

        if (membership.getStatus() != MembershipStatus.CREATED)
            throw new BusinessException(MEMBERSHIP_STATUS_NOT_ACTIVATABLE);

        boolean hasActive = membershipRepository.existsByClientIdAndStatus(
                membership.getClient().getId(),
                MembershipStatus.ACTIVE
        );

        if (hasActive) throw new BusinessException(CLIENT_ALREADY_HAS_ACTIVE_MEMBERSHIP);

        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setStartDate(LocalDateTime.now());

        if (membership.getType() != MembershipType.VISITS) {
            if (membership.getDurationMonths() == null) throw new BusinessException(MEMBERSHIP_DURATION_REQUIRED);

            membership.setEndDate(
                    membership.getStartDate().plusMonths(membership.getDurationMonths())
            );
        }

        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse freezeMembership(String membershipId) {
        Membership membership = findMembershipById(membershipId);

        if (membership.getType() == MembershipType.VISITS) throw new BusinessException(MEMBERSHIP_TYPE_NOT_FREEZABLE);
        if (membership.getStatus() != MembershipStatus.ACTIVE)
            throw new BusinessException(MEMBERSHIP_STATUS_NOT_FREEZABLE);

        membership.setStatus(MembershipStatus.FROZEN);
        membership.setFreezeDate(LocalDateTime.now());

        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse unfreezeMembership(String membershipId) {
        Membership membership = findMembershipById(membershipId);

        if (membership.getStatus() != MembershipStatus.FROZEN || membership.getFreezeDate() == null)
            throw new BusinessException(MEMBERSHIP_NOT_FROZEN);


        if (membership.getEndDate() != null) {
            Duration frozenDuration = Duration.between(
                    membership.getFreezeDate(),
                    LocalDateTime.now()
            );

            membership.setEndDate(membership.getEndDate().plus(frozenDuration));
        }

        membership.setFreezeDate(null);
        membership.setStatus(MembershipStatus.ACTIVE);

        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse extendMembership(String membershipId, ExtendMembershipRequest request) {
        Membership membership = findMembershipById(membershipId);

        if (membership.getType() == MembershipType.VISITS) throw new BusinessException(MEMBERSHIP_TYPE_NOT_EXTENDABLE);
        if (membership.getStatus() != MembershipStatus.ACTIVE && membership.getStatus() != MembershipStatus.FROZEN)
            throw new BusinessException(MEMBERSHIP_STATUS_NOT_EXTENDABLE);

        if (membership.getEndDate() == null) throw new BusinessException(MEMBERSHIP_END_DATE_MISSING);
        membership.setEndDate(membership.getEndDate().plusMonths(request.getMonths()));
        membershipRepository.save(membership);

        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional
    public MembershipResponse cancelMembership(String membershipId) {
        Membership membership = findMembershipById(membershipId);
        if (membership.getStatus() == MembershipStatus.CANCELLED)
            throw new BusinessException(MEMBERSHIP_STATUS_NOT_CANCELLABLE);

        switch (membership.getStatus()) {
            case ACTIVE:
                membership.setStatus(MembershipStatus.CANCELLED);
                membership.setEndDate(LocalDateTime.now());
                break;
            case FROZEN:
                membership.setStatus(MembershipStatus.CANCELLED);
                membership.setFreezeDate(null);
                membership.setEndDate(LocalDateTime.now());
                break;
            case CREATED:
                membership.setStatus(MembershipStatus.CANCELLED);
                break;
            default:
                throw new BusinessException(MEMBERSHIP_STATUS_NOT_CANCELLABLE);
        }

        membershipRepository.save(membership);
        return membershipMapper.toResponse(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MembershipResponse> getMembershipByClientId(int page, int size, String clientId) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<Membership> membershipPage = membershipRepository.findMembershipsByClientId(clientId, pageable);

        return PaginationUtils.toPageResponse(membershipPage, membershipMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipResponse getMembershipByClientIdAndActive() {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();

        return membershipRepository.findMembershipByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE)
                .map(membershipMapper::toResponse)
                .orElseThrow(() -> new BusinessException(NO_ACTIVE_MEMBERSHIP));
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipHistoryResponse getMembershipClientHistory() {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        List<Membership> memberships = membershipRepository.findAllByClientIdOrderByCreatedDateDesc(client.getId());

        return membershipMapper.toHistoryResponse(memberships);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipValidationResponse validateMembership(String clientId) {
        Optional<Membership> optionalMembership = membershipRepository.findMembershipByClientIdAndStatus(
                clientId,
                MembershipStatus.ACTIVE
        );

        if (optionalMembership.isEmpty())
            return MembershipValidationResponse.builder()
                    .valid(false)
                    .reason("NO_ACTIVE_MEMBERSHIP")
                    .build();

        Membership membership = optionalMembership.get();
        if (membership.getStatus() == MembershipStatus.FROZEN)
            return MembershipValidationResponse.builder()
                    .valid(false)
                    .reason("MEMBERSHIP_FROZEN")
                    .build();

        if (membership.getType() == MembershipType.VISITS) {
            if (membership.getVisitsLeft() == null || membership.getVisitsLeft() <= 0)
                return MembershipValidationResponse.builder()
                        .valid(false)
                        .reason("VISITS_LIMIT_REACHED")
                        .build();
        } else {
            if (membership.getEndDate() != null && membership.getEndDate().isBefore(LocalDateTime.now()))
                return MembershipValidationResponse.builder()
                        .valid(false)
                        .reason("MEMBERSHIP_EXPIRED")
                        .build();
        }

        return MembershipValidationResponse.builder()
                .valid(true)
                .reason("OK")
                .build();
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

    private Membership findMembershipById(String membershipId) {
        log.info("Finding membership by id: {}", membershipId);
        return membershipRepository.findById(membershipId).orElseThrow(() -> new BusinessException(MEMBERSHIP_NOT_FOUND));
    }
}
