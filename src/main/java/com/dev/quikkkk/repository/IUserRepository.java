package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.roles
            WHERE u.email = :email
            """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {
            "roles",
            "trainerProfile",
            "trainerProfile.specialization",
            "clientProfile"
    })
    @NullMarked
    Optional<User> findById(String id);
}
