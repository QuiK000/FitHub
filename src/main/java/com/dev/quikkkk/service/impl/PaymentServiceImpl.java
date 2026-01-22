package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreatePaymentRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.PaymentResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.entity.Payment;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.PaymentMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.repository.IPaymentRepository;
import com.dev.quikkkk.service.IMembershipService;
import com.dev.quikkkk.service.IPaymentService;
import com.dev.quikkkk.utils.PaginationUtils;
import com.dev.quikkkk.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_MEMBERSHIP_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_ALREADY_ACTIVATED;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_CANCELLED;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_EXPIRED;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {
    private final IPaymentRepository paymentRepository;
    private final IMembershipRepository membershipRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IMembershipService membershipService;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        ClientProfile client = findClientProfile();
        Membership membership = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new BusinessException(MEMBERSHIP_NOT_FOUND));

        if (!membership.getClient().getId().equals(client.getId()))
            throw new BusinessException(CLIENT_MEMBERSHIP_NOT_FOUND);

        switch (membership.getStatus()) {
            case ACTIVE:
                throw new BusinessException(MEMBERSHIP_ALREADY_ACTIVATED);
            case EXPIRED:
                throw new BusinessException(MEMBERSHIP_EXPIRED);
            case CANCELLED:
                throw new BusinessException(MEMBERSHIP_CANCELLED);
        }

        Payment payment = paymentMapper.toEntity(request, membership);
        paymentRepository.save(payment);

        membership.setPayment(payment);
        membershipRepository.save(membership);

        membershipService.activateMembership(membership.getId());

        log.info("Payment processed and membership activated successfully for id: {}", membership.getId());
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPayments(int page, int size) {
        ClientProfile client = findClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "paymentDate");
        Page<Payment> paymentPage = paymentRepository.findPaymentsByClientId(client.getId(), pageable);

        return PaginationUtils.toPageResponse(paymentPage, paymentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByClientId(String clientId, int page, int size) {
        ClientProfile client = clientProfileRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "paymentDate");
        Page<Payment> paymentPage = paymentRepository.findPaymentsByClientId(client.getId(), pageable);

        return PaginationUtils.toPageResponse(paymentPage, paymentMapper::toResponse);
    }

    private ClientProfile findClientProfile() {
        return clientProfileRepository.findByUserId(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new BusinessException(CLIENT_PROFILE_NOT_FOUND));
    }
}
