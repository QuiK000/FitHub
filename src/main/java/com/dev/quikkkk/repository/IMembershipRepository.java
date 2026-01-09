package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMembershipRepository extends JpaRepository<Membership, String> {
    boolean existsByClientIdAndStatus(String clientId, MembershipStatus status);
}
