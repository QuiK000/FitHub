package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITrainingSessionRepository extends JpaRepository<TrainingSession, String> {
}
