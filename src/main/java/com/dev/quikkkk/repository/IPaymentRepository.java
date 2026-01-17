package com.dev.quikkkk.repository;

import com.dev.quikkkk.dto.response.RevenueStatsResponse;
import com.dev.quikkkk.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, String> {
    @Query("FROM Payment p WHERE p.client.id = :clientId")
    Page<Payment> findPaymentsByClientId(@Param("clientId") String clientId, Pageable pageable);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID'")
    BigDecimal findPaymentsWhereStatusPaid();

    @Query("""
            SELECT new com.dev.quikkkk.dto.response.RevenueStatsResponse(
                        CAST(p.createdDate AS DATE) AS date,
                        COALESCE(SUM(p.amount), 0) AS revenue
            )
            FROM Payment p
            WHERE p.createdDate >= :from
            AND p.createdDate < :to
            GROUP BY CAST(p.createdDate AS DATE)
            ORDER BY CAST(p.createdDate AS DATE)
            """)
    List<RevenueStatsResponse> findRevenueStatsByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
