package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.enums.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMembershipRepository extends JpaRepository<Membership, String> {
    boolean existsByClientIdAndStatus(String clientId, MembershipStatus status);

    Page<Membership> findMembershipsByClientId(String clientId, Pageable pageable);

    Optional<Membership> findMembershipByClientIdAndStatus(String clientId, MembershipStatus status);

    List<Membership> findAllByClientIdOrderByCreatedDateDesc(String clientId);
}
