package com.dev.quikkkk.modules.workout.repository;

import com.dev.quikkkk.modules.workout.entity.SessionWaitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWaitlistRepository extends JpaRepository<SessionWaitlist, String> {
}
