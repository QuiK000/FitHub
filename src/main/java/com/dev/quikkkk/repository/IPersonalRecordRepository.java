package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.PersonalRecord;
import com.dev.quikkkk.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPersonalRecordRepository extends JpaRepository<PersonalRecord, String> {
    @Query("""
            SELECT pr FROM PersonalRecord pr WHERE pr.client.id = :clientId
            AND pr.exercise.id = :exerciseId
            AND pr.recordType = :type
            AND pr.isCurrentBest = true
            """)
    Optional<PersonalRecord> findCurrentBest(
            @Param("clientId") String clientId,
            @Param("exerciseId") String exerciseId,
            @Param("type") RecordType type
    );

    Page<PersonalRecord> findAllByClientId(String clientId, Pageable pageable);

    Page<PersonalRecord> findPersonalRecordByExerciseIdAndClientId(String exerciseId, String clientId, Pageable pageable);

    Page<PersonalRecord> findByClientIdOrderByCreatedDateDesc(String clientId, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE PersonalRecord pr SET pr.isCurrentBest = false
            WHERE pr.client.id = :clientId
            AND pr.exercise.id = :exerciseId
            AND pr.recordType = :type
            """)
    void disableOldBests(
            @Param("clientId") String clientId,
            @Param("exerciseId") String exerciseId,
            @Param("type") RecordType type
    );
}
