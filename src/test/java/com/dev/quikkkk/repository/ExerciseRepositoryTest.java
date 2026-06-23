package com.dev.quikkkk.repository;

import com.dev.quikkkk.config.TestCacheConfig;
import com.dev.quikkkk.integration.AbstractIntegrationTest;
import com.dev.quikkkk.modules.workout.entity.Exercise;
import com.dev.quikkkk.modules.workout.enums.ExerciseCategory;
import com.dev.quikkkk.modules.workout.enums.MuscleGroup;
import com.dev.quikkkk.modules.workout.repository.IExerciseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestCacheConfig.class)
@DisplayName("ExerciseRepository Tests")
class ExerciseRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IExerciseRepository exerciseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find active exercises with optional search")
    void findActiveWithOptionalSearch_WithSearchTerm_ReturnsMatchingExercises() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistExercise("Bench Press " + marker, ExerciseCategory.STRENGTH, MuscleGroup.CHEST, true);
        persistExercise("Squat " + marker, ExerciseCategory.STRENGTH, MuscleGroup.QUADS, true);
        persistExercise("Inactive " + marker, ExerciseCategory.STRENGTH, MuscleGroup.CHEST, false);

        Page<Exercise> result = exerciseRepository.findActiveWithOptionalSearch(
                "Bench", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).contains("Bench Press");
    }

    @Test
    @DisplayName("Should find all active exercises")
    void findAllExercisesByActiveTrue_WithMixedActiveAndInactive_ReturnsOnlyActive() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistExercise("Active " + marker, ExerciseCategory.CARDIO, MuscleGroup.FULL_BODY, true);
        persistExercise("Inactive " + marker, ExerciseCategory.CARDIO, MuscleGroup.FULL_BODY, false);

        Page<Exercise> result = exerciseRepository.findAllExercisesByActiveTrue(PageRequest.of(0, 10));

        assertThat(result.getContent()).allMatch(Exercise::isActive);
    }

    @Test
    @DisplayName("Should find exercises by category")
    void findAllByActiveTrueAndCategory_WithCategory_ReturnsMatchingExercises() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistExercise("Bench Press " + marker, ExerciseCategory.STRENGTH, MuscleGroup.CHEST, true);
        persistExercise("Running " + marker, ExerciseCategory.CARDIO, MuscleGroup.FULL_BODY, true);

        Page<Exercise> result = exerciseRepository.findAllExercisesByActiveTrueAndCategory(
                ExerciseCategory.STRENGTH, PageRequest.of(0, 10));

        assertThat(result.getContent()).allMatch(e -> e.getCategory() == ExerciseCategory.STRENGTH);
    }

    @Test
    @DisplayName("Should find exercises by muscle group")
    void findAllByActiveTrueAndMuscleGroup_WithMuscleGroup_ReturnsMatchingExercises() {
        String marker = UUID.randomUUID().toString().substring(0, 8);
        persistExercise("Bench Press " + marker, ExerciseCategory.STRENGTH, MuscleGroup.CHEST, true);
        persistExercise("Squat " + marker, ExerciseCategory.STRENGTH, MuscleGroup.QUADS, true);

        Page<Exercise> result = exerciseRepository.findAllExercisesByActiveTrueAndMuscleGroup(
                MuscleGroup.CHEST, PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getPrimaryMuscleGroup()).isEqualTo(MuscleGroup.CHEST);
    }

    @Test
    @DisplayName("Should find exercise by id with secondary muscles")
    void findByIdWithSecondaryMuscles_WithExistingExercise_ReturnsExercise() {
        Exercise exercise = persistExercise("Push Up", ExerciseCategory.STRENGTH, MuscleGroup.CHEST, true);

        Optional<Exercise> found = exerciseRepository.findByIdWithSecondaryMuscles(exercise.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Push Up");
    }

    @Test
    @DisplayName("Should return empty when exercise not found")
    void findByIdWithSecondaryMuscles_WithNonExistingId_ReturnsEmpty() {
        Optional<Exercise> found = exerciseRepository.findByIdWithSecondaryMuscles(UUID.randomUUID().toString());

        assertThat(found).isEmpty();
    }

    private Exercise persistExercise(String name, ExerciseCategory category, MuscleGroup muscleGroup, boolean active) {
        Exercise exercise = Exercise.builder()
                .name(name)
                .description("Description for " + name)
                .category(category)
                .primaryMuscleGroup(muscleGroup)
                .active(active)
                .createdDate(LocalDateTime.now())
                .createdBy("test")
                .build();
        entityManager.persist(exercise);
        entityManager.flush();
        return exercise;
    }
}
