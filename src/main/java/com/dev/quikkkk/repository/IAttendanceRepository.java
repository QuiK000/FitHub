package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAttendanceRepository extends JpaRepository<Attendance, String> {
    boolean existsByClientIdAndSessionId(String clientId, String sessionId);

    List<Attendance> findAllByClientId(String id);

    List<Attendance> findALlBySessionId(String sessionId);
}
