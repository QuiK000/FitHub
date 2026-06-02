package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.membership.dto.request.CreatePaymentRequest;
import com.dev.quikkkk.modules.membership.entity.Membership;
import com.dev.quikkkk.modules.membership.entity.Payment;
import com.dev.quikkkk.modules.membership.enums.MembershipStatus;
import com.dev.quikkkk.modules.membership.enums.PaymentCurrency;
import com.dev.quikkkk.modules.membership.enums.PaymentStatus;
import com.dev.quikkkk.modules.membership.mapper.PaymentMapper;
import com.dev.quikkkk.modules.membership.repository.IMembershipRepository;
import com.dev.quikkkk.modules.membership.repository.IPaymentRepository;
import com.dev.quikkkk.modules.membership.service.IMembershipService;
import com.dev.quikkkk.modules.membership.service.impl.PaymentServiceImpl;
import com.dev.quikkkk.modules.membership.utils.TronPaymentValidator;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IMembershipRepository membershipRepository;

    @Mock
    private IMembershipService membershipService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private ClientProfileUtils clientProfileUtils;

    @Mock
    private TronPaymentValidator tronPaymentValidator;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private ClientProfile client;
    private Membership membership;
    private CreatePaymentRequest request;

    @BeforeEach
    void setUp() {
        client = new ClientProfile();
        client.setId("client-123");

        membership = new Membership();
        membership.setId("membership-123");
        membership.setClient(client);
        membership.setStatus(MembershipStatus.CREATED);

        request = new CreatePaymentRequest();
        request.setMembershipId("membership-123");
        request.setCurrency(PaymentCurrency.TRX);
        request.setTransactionHash("trx-hash");
        request.setAmount(BigDecimal.valueOf(100));
    }

    @Test
    void createPayment_ShouldProcessSuccessfulPayment() {
        Payment payment = new Payment();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        when(paymentMapper.toEntity(any(), any())).thenReturn(payment);
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(payment);

        paymentService.createPayment(request);

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals("trx-hash", payment.getTransactionHash());

        verify(tronPaymentValidator).validateTransaction(request.getTransactionHash(), request.getAmount());
        verify(membershipService).processSuccessfulPayment(eq(membership.getId()), same(payment));
    }

    @Test
    void createPayment_ShouldThrowWhenMembershipNotFound() {
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> paymentService.createPayment(request));
        assertEquals(ErrorCode.MEMBERSHIP_NOT_FOUND, exception.getErrorCode());

        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(membershipService);
    }

    @Test
    void createPayment_ShouldThrowWhenMembershipBelongsToAnotherClient() {
        ClientProfile anotherClient = new ClientProfile();

        anotherClient.setId("another-client");
        membership.setClient(anotherClient);

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.createPayment(request));

        assertEquals(ErrorCode.CLIENT_MEMBERSHIP_NOT_FOUND, exception.getErrorCode());
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void createPayment_ShouldThrowWhenMembershipAlreadyActive() {
        membership.setStatus(MembershipStatus.ACTIVE);

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.createPayment(request));

        assertEquals(ErrorCode.MEMBERSHIP_ALREADY_ACTIVATED, exception.getErrorCode());
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void createPayment_ShouldThrowWhenTransactionHashIsBlank() {
        request.setTransactionHash(" ");
        Payment payment = new Payment();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        when(paymentMapper.toEntity(any(), any())).thenReturn(payment);
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.createPayment(request));

        assertEquals(ErrorCode.PAYMENT_VALIDATION_ERROR, exception.getErrorCode());
        verify(paymentRepository, never()).saveAndFlush(any());
    }

    @Test
    void createPayment_ShouldThrowWhenTransactionAlreadyUsed() {
        Payment payment = new Payment();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        when(paymentMapper.toEntity(any(), any())).thenReturn(payment);
        when(paymentRepository.saveAndFlush(any(Payment.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));
        BusinessException exception = assertThrows(BusinessException.class,
                () -> paymentService.createPayment(request));

        assertEquals(ErrorCode.TRANSACTION_ALREADY_USED, exception.getErrorCode());
        verify(membershipService, never()).processSuccessfulPayment(anyString(), any());
    }

    @Test
    void createPayment_ShouldThrowWhenMembershipExpired() {
        membership.setStatus(MembershipStatus.EXPIRED);

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.createPayment(request));
        assertEquals(ErrorCode.MEMBERSHIP_EXPIRED, exception.getErrorCode());
    }

    @Test
    void createPayment_ShouldThrowWhenMembershipFrozen() {
        membership.setStatus(MembershipStatus.FROZEN);

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.createPayment(request));
        assertEquals(ErrorCode.MEMBERSHIP_FROZEN, exception.getErrorCode());
    }

    @Test
    void createPayment_ShouldThrowWhenMembershipCancelled() {
        membership.setStatus(MembershipStatus.CANCELLED);

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.createPayment(request));
        assertEquals(ErrorCode.MEMBERSHIP_CANCELLED, exception.getErrorCode());
    }
}
