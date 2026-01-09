package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.TrainerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<TrainerProfile> findTrainerProfileById(String id);

    @Query("""
            SELECT t FROM TrainerProfile t
            LEFT JOIN t.specialization s
            WHERE t.active = true
            AND (
                :search IS NULL
                OR TRIM(:search) = ''
                OR LOWER (t.firstname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER (t.lastname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<TrainerProfile> findActiveWithOptionalSearch(
            @Param("search") String search,
            Pageable pageable
    );
}
