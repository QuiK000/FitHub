package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITrainerProfileRepository extends JpaRepository<TrainerProfile, String> {

}
