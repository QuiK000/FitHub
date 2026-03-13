package com.dev.quikkkk.modules.user.repository;

import com.dev.quikkkk.modules.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}
