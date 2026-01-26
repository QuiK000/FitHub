package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.enums.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IMembershipRepository extends JpaRepository<Membership, String> {
    boolean existsByClientIdAndStatus(String clientId, MembershipStatus status);

    Page<Membership> findMembershipsByClientId(String clientId, Pageable pageable);

    Optional<Membership> findMembershipByClientIdAndStatus(String clientId, MembershipStatus status);

    List<Membership> findAllByClientIdOrderByCreatedDateDesc(String clientId);

    List<Membership> findByStatusAndEndDateBefore(MembershipStatus status, LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.status = 'ACTIVE'")
    Integer findAllActiveMemberships();

    @Modifying
    @Query("UPDATE Membership m SET m.visitsLeft = m.visitsLeft - 1 WHERE m.id = :id AND m.visitsLeft > 0")
    int decrementVisits(@Param("id") String id);
}
