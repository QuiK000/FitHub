package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, String> {
    @Query("SELECT p FROM Payment p WHERE p.client.id = :clientId")
    Page<Payment> findPaymentsByClientId(@Param("clientId") String clientId, Pageable pageable);
}
