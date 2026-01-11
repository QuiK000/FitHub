package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.enums.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClientProfileRepository extends JpaRepository<ClientProfile, String> {
    @Query("SELECT c FROM ClientProfile c WHERE c.user.id = :userId")
    Optional<ClientProfile> findByUserId(@Param("userId") String userId);

    @Query("""
            SELECT c FROM ClientProfile c
            WHERE c.active = true
            AND (
                    :search IS NULL
                    OR TRIM(:search) = ''
                    OR LOWER(c.lastname) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(c.firstname) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
    Page<ClientProfile> findActiveWithOptionalSearch(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT c FROM ClientProfile c
            JOIN c.memberships m
            WHERE m.status = :status
            AND c.user.id = :userId
            """)
    Optional<ClientProfile> findByUserIdAndActiveMembership(
            @Param("userId") String userId,
            @Param("status") MembershipStatus status
    );
}
