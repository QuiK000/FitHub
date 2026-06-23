package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.membership.entity.Payment;
import com.dev.quikkkk.modules.membership.enums.PaymentCurrency;
import com.dev.quikkkk.modules.membership.enums.PaymentStatus;
import com.dev.quikkkk.modules.membership.repository.IPaymentRepository;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
@DisplayName("PaymentRepository Tests")
class PaymentRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find payments by client id")
    void findPaymentsByClientId_WithPayments_ReturnsPage() {
        ClientProfile client = persistClient();
        persistPayment(client, new BigDecimal("100.00"), PaymentStatus.PAID, "hash-1");
        persistPayment(client, new BigDecimal("50.00"), PaymentStatus.PAID, "hash-2");

        Page<Payment> result = paymentRepository.findPaymentsByClientId(
                client.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should sum paid payments")
    void findPaymentsWhereStatusPaid_WithPaidPayments_ReturnsSum() {
        ClientProfile client = persistClient();
        persistPayment(client, new BigDecimal("100.00"), PaymentStatus.PAID, "sum-hash-1");
        persistPayment(client, new BigDecimal("200.00"), PaymentStatus.PAID, "sum-hash-2");
        persistPayment(client, new BigDecimal("50.00"), PaymentStatus.PENDING, "sum-hash-3");

        BigDecimal total = paymentRepository.findPaymentsWhereStatusPaid();

        assertThat(total).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Should check transaction hash existence")
    void existsByTransactionHash_WithExistingHash_ReturnsTrue() {
        ClientProfile client = persistClient();
        String hash = "unique-hash-" + UUID.randomUUID();
        persistPayment(client, new BigDecimal("100.00"), PaymentStatus.PAID, hash);

        boolean exists = paymentRepository.existsByTransactionHash(hash);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for non-existing transaction hash")
    void existsByTransactionHash_WithNonExistingHash_ReturnsFalse() {
        boolean exists = paymentRepository.existsByTransactionHash("nonexistent-hash");

        assertThat(exists).isFalse();
    }

    private ClientProfile persistClient() {
        User user = User.builder()
                .email("payment-user-" + UUID.randomUUID() + "@test.com")
                .password("encoded")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(user);

        ClientProfile client = ClientProfile.builder()
                .firstname("Payment")
                .lastname("Client")
                .active(true)
                .user(user)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(client);
        entityManager.flush();
        return client;
    }

    private Payment persistPayment(ClientProfile client, BigDecimal amount, PaymentStatus status, String hash) {
        Payment payment = Payment.builder()
                .amount(amount)
                .currency(PaymentCurrency.TRX)
                .status(status)
                .transactionHash(hash)
                .paymentDate(LocalDateTime.now())
                .client(client)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(payment);
        entityManager.flush();
        return payment;
    }
}
