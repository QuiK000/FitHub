package com.dev.quikkkk.repository;

import com.dev.quikkkk.dto.response.AttendanceStatsResponse;
import com.dev.quikkkk.dto.response.PopularSessionResponse;
import com.dev.quikkkk.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
            SELECT new com.dev.quikkkk.dto.response.PopularSessionResponse(
            s.id,
            CONCAT(t.firstname, ' ', t.lastname),
            COUNT(a)
            )
            FROM Attendance a
            JOIN a.session s
            JOIN s.trainer t
            GROUP BY s.id, t.firstname, t.lastname
            ORDER BY COUNT(a) DESC
            """)
    Page<PopularSessionResponse> findTopSessions(Pageable pageable);

    @Query("""
            SELECT COUNT(DISTINCT a.client.id)
            FROM Attendance a
            JOIN a.session s
            WHERE s.trainer.id = :trainerId
            """)
    long countAllClientsByTrainer(@Param("trainerId") String trainerId);

    @Query("""
            SELECT COUNT(a)
            FROM Attendance a
            JOIN a.session s
            WHERE s.trainer.id = :trainerId
            """)
    long countAllAttendancesByTrainer(@Param("trainerId") String trainerId);

    @Query("""
            SELECT COUNT(a.session)
            FROM Attendance a
            WHERE a.client.id = :clientId
            """)
    long countTotalVisitsByClient(@Param("clientId") String clientId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.client.id = :clientId")
    long countAttendedSessionsByClient(@Param("clientId") String clientId);

    @Query("""
            SELECT MAX(a.checkInTime)
            FROM Attendance a
            WHERE a.client.id = :clientId
            """)
    LocalDateTime findLastVisitByClient(@Param("clientId") String clientId);

    @Query("""
            SELECT new com.dev.quikkkk.dto.response.AttendanceStatsResponse(
                        FUNCTION('DATE', a.checkInTime),
                        COUNT(a)
            )
            FROM Attendance a
            WHERE a.checkInTime >= :from
            AND a.checkInTime < :to
            GROUP BY FUNCTION('DATE', a.checkInTime)
            ORDER BY FUNCTION('DATE', a.checkInTime)
            """)
    List<AttendanceStatsResponse> findAttendanceStatsByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
