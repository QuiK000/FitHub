package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.ProgressPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProgressPhotoRepository extends JpaRepository<ProgressPhoto, String> {
    Page<ProgressPhoto> findProgressPhotosByClientId(String clientId, Pageable pageable);
}
