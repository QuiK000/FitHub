package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientProfileRepository extends JpaRepository<ClientProfile, String> {
}
