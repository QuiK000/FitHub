package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ISpecializationRepository extends JpaRepository<Specialization, String> {
    Set<Specialization> findByIdIn(Set<String> ids);

    boolean existsByNameIgnoreCase(String name);

    Page<Specialization> findAllByActiveTrue(Pageable pageable);

    Page<Specialization> findAllByActiveTrueAndNameContainingIgnoreCase(String search, Pageable pageable);
}
