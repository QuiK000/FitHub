package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IAttendanceRepository extends JpaRepository<Attendance, String> {
    boolean existsByClientIdAndSessionId(String clientId, String sessionId);

    List<Attendance> findAllByClientId(String id);

    List<Attendance> findALlBySessionId(String sessionId);

    @Query("""
            SELECT COUNT(a)
            FROM Attendance a
            WHERE a.createdDate >= :start
            AND a.createdDate < :end
            """)
    Integer countAttendanceByToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
