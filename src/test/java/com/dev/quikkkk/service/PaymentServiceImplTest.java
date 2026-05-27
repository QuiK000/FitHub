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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    private ClientProfile mockClient;
    private Membership mockMembership;
    private CreatePaymentRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockClient = new ClientProfile();
        mockClient.setId("client-123");

        mockMembership = new Membership();
        mockMembership.setId("mem-123");
        mockMembership.setClient(mockClient);
        mockMembership.setStatus(MembershipStatus.CREATED);

        mockRequest = new CreatePaymentRequest();
        mockRequest.setMembershipId("mem-123");
        mockRequest.setCurrency(PaymentCurrency.TRX);
        mockRequest.setTransactionHash("hash-123");
        mockRequest.setAmount(BigDecimal.valueOf(100.0));
    }

    @Test
    void createPayment_Success_ActivatesMembership() {
        Payment mockPayment = new Payment();
        mockPayment.setStatus(PaymentStatus.PENDING);

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(mockClient);
        when(membershipRepository.findById("mem-123")).thenReturn(Optional.of(mockMembership));
        when(paymentMapper.toEntity(any(), any())).thenReturn(mockPayment);
        when(paymentRepository.existsByTransactionHash(anyString())).thenReturn(false);
        when(paymentRepository.saveAndFlush(any())).thenReturn(mockPayment);

        paymentService.createPayment(mockRequest);

        assertEquals("hash-123", mockPayment.getTransactionHash());
        assertEquals(PaymentStatus.PAID, mockPayment.getStatus());

        verify(membershipService, times(1)).activateMembership("mem-123");
        verify(membershipRepository, times(1)).save(mockMembership);
    }

    @Test
    void createPayment_RaceConditionDetected_ThrowsException() {
        Payment mockPayment = new Payment();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(mockClient);
        when(membershipRepository.findById("mem-123")).thenReturn(Optional.of(mockMembership));
        when(paymentMapper.toEntity(any(), any())).thenReturn(mockPayment);
        when(paymentRepository.existsByTransactionHash(anyString())).thenReturn(false);

        when(paymentRepository.saveAndFlush(any())).thenThrow(DataIntegrityViolationException.class);
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.createPayment(mockRequest));

        assertEquals(ErrorCode.TRANSACTION_ALREADY_USED, exception.getErrorCode());
        verify(membershipService, never()).activateMembership(anyString());
    }
}
