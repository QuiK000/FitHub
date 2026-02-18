package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.PersonalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonalRecordRepository extends JpaRepository<PersonalRecord, String> {
    Page<PersonalRecord> findAllByClientId(String clientId, Pageable pageable);

    Page<PersonalRecord> findPersonalRecordByExerciseIdAndClientId(String exerciseId, String clientId, Pageable pageable);

    Page<PersonalRecord> findByClientIdOrderByCreatedDateDesc(String clientId, Pageable pageable);
}
