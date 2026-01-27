package com.dev.quikkkk.repository;

import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IExerciseRepository extends JpaRepository<Exercise, String> {
    @Query("""
            SELECT e FROM Exercise e
            WHERE e.active = true
            AND (
                 :search IS NULL
                  OR TRIM(:search) = ''
                  OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(e.instructions) LIKE LOWER(CONCAT('%', :search, '%'))
                ) ORDER BY e.createdDate DESC
            """)
    Page<Exercise> findActiveWithOptionalSearch(@Param("search") String search, Pageable pageable);

    Page<Exercise> findAllExercisesByActiveTrue(Pageable pageable);

    Page<Exercise> findAllExercisesByActiveTrueAndCategory(ExerciseCategory category, Pageable pageable);

    @Query("""
            SELECT DISTINCT e FROM Exercise e
            LEFT JOIN e.secondaryMuscleGroups smg
            WHERE e.active = true
            AND (
                e.primaryMuscleGroup = :muscleGroup
             OR smg = :muscleGroup
            ) ORDER BY e.createdDate DESC
            """)
    Page<Exercise> findAllExercisesByActiveTrueAndMuscleGroup(@Param("muscleGroup") MuscleGroup muscleGroup, Pageable pageable);

    @Query("""
            SELECT DISTINCT e
            FROM Exercise e
            LEFT JOIN FETCH e.secondaryMuscleGroups
            WHERE e.id = :id
            """)
    Optional<Exercise> findByIdWithSecondaryMuscles(String id);
}
