package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ISpecializationRepository extends JpaRepository<Specialization, String> {
    Set<Specialization> findByIdInAndActiveTrue(Set<String> ids);

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            SELECT s FROM Specialization s
            WHERE s.active = true
            AND (
                        :search IS NULL
                        OR TRIM(:search) = ''
                        OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
    Page<Specialization> findActiveWithOptionalSearch(@Param("search") String search, Pageable pageable);
}
