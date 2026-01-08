package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.TrainerProfile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITrainerProfileRepository extends JpaRepository<TrainerProfile, String> {
    @Query("""
            SELECT t FROM TrainerProfile t
            LEFT JOIN FETCH t.specialization
            WHERE t.user.id = :userId
            """)
    Optional<TrainerProfile> findByUserIdWithSpecializations(@Param("userId") String id);
}
