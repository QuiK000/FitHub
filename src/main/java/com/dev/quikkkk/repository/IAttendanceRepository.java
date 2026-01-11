package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAttendanceRepository extends JpaRepository<Attendance, String> {
    boolean existsByClientIdAndSessionId(String clientId, String sessionId);
}
