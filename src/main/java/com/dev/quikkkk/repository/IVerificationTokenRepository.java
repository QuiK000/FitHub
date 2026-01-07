package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.entity.VerificationToken;
import com.dev.quikkkk.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IVerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    @Modifying
    @Query("""
            UPDATE VerificationToken v
            SET v.used = true
            WHERE v.user = :user
            AND v.type = :type
            AND v.used = false
            """)
    void invalidateAllByUserAndType(@Param("user") User user, @Param("type") TokenType type);

    Optional<VerificationToken> findByTokenAndUsedFalse(String token);

    @Modifying
    @Query("""
        DELETE FROM VerificationToken v
        WHERE v.used = true
        OR v.expiresAt < :now
        """)
    int deleteExpiredOrUsed(@Param("now")LocalDateTime now);
}
