package com.dev.quikkkk.modules.membership.service.impl;

import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.core.utils.PaginationUtils;
import com.dev.quikkkk.modules.membership.dto.request.CreatePaymentRequest;
import com.dev.quikkkk.modules.membership.dto.response.PaymentResponse;
import com.dev.quikkkk.modules.membership.entity.Membership;
import com.dev.quikkkk.modules.membership.entity.Payment;
import com.dev.quikkkk.modules.membership.enums.PaymentStatus;
import com.dev.quikkkk.modules.membership.mapper.PaymentMapper;
import com.dev.quikkkk.modules.membership.repository.IMembershipRepository;
import com.dev.quikkkk.modules.membership.repository.IPaymentRepository;
import com.dev.quikkkk.modules.membership.service.IMembershipService;
import com.dev.quikkkk.modules.membership.service.IPaymentService;
import com.dev.quikkkk.modules.membership.utils.TronPaymentValidator;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.repository.IClientProfileRepository;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_MEMBERSHIP_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.CLIENT_PROFILE_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_ALREADY_ACTIVATED;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_CANCELLED;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_EXPIRED;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_FROZEN;
import static com.dev.quikkkk.core.enums.ErrorCode.MEMBERSHIP_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.PAYMENT_VALIDATION_ERROR;
import static com.dev.quikkkk.core.enums.ErrorCode.TRANSACTION_ALREADY_USED;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {
    private final IPaymentRepository paymentRepository;
    private final IMembershipRepository membershipRepository;
    private final IClientProfileRepository clientProfileRepository;
    private final IMembershipService membershipService;
    private final PaymentMapper paymentMapper;
    private final ClientProfileUtils clientProfileUtils;
    private final TronPaymentValidator tronPaymentValidator;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Membership membership = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new BusinessException(MEMBERSHIP_NOT_FOUND));

        if (!membership.getClient().getId().equals(client.getId()))
            throw new BusinessException(CLIENT_MEMBERSHIP_NOT_FOUND);

        switch (membership.getStatus()) {
            case ACTIVE -> throw new BusinessException(MEMBERSHIP_ALREADY_ACTIVATED);
            case EXPIRED -> throw new BusinessException(MEMBERSHIP_EXPIRED);
            case CANCELLED -> throw new BusinessException(MEMBERSHIP_CANCELLED);
            case FROZEN -> throw new BusinessException(MEMBERSHIP_FROZEN);
        }

        Payment payment = paymentMapper.toEntity(request, membership);

        payment.setClient(client);
        payment.setPaymentDate(LocalDateTime.now());

        switch (request.getCurrency()) {
            case TRX -> processTronPayment(request, payment);
            case BTC, ETH, USDT -> log.debug("BTC, ETH, USDT"); // TODO
            case USD, EUR, UAH -> log.debug("USD, EUR, UAH"); // TODO
            default -> {
                log.warn("Unsupported currency attempted: {}", request.getCurrency());
                throw new BusinessException(PAYMENT_VALIDATION_ERROR);
            }
        }

        try {
            payment = paymentRepository.saveAndFlush(payment);
        } catch (DataIntegrityViolationException e) {
            log.error("Transaction hash collision detected for hash: {}", request.getTransactionHash());
            throw new BusinessException(TRANSACTION_ALREADY_USED);
        }

        membership.setPayment(payment);
        membershipRepository.save(membership);

        if (PaymentStatus.PAID.equals(payment.getStatus())) {
            membershipService.activateMembership(membership.getId());
            log.info("Membership {} automatically activated via service.", membership.getId());
        }

        log.info("Payment process completed successfully for membership id: {}", membership.getId());
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPayments(int page, int size) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
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

    private void processTronPayment(CreatePaymentRequest request, Payment payment) {
        if (request.getTransactionHash() == null || request.getTransactionHash().isBlank())
            throw new BusinessException(PAYMENT_VALIDATION_ERROR);

        if (paymentRepository.existsByTransactionHash(request.getTransactionHash()))
            throw new BusinessException(TRANSACTION_ALREADY_USED);

        tronPaymentValidator.validateTransaction(request.getTransactionHash(), request.getAmount());

        payment.setTransactionHash(request.getTransactionHash());
        payment.setStatus(PaymentStatus.PAID);

        log.info("TRX payment validated successfully: {}", request.getTransactionHash());
    }
}
