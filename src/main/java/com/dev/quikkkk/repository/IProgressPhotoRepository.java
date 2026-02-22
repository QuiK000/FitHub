package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.ProgressPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProgressPhotoRepository extends JpaRepository<ProgressPhoto, String> {
    @EntityGraph(attributePaths = {"measurement"})
    Page<ProgressPhoto> findAllByClientId(String clientId, Pageable pageable);

    @EntityGraph(attributePaths = {"measurement"})
    Optional<ProgressPhoto> findByIdAndClientId(String id, String clientId);

    boolean existsByIdAndClientId(String id, String clientId);
}
