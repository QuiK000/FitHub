package com.dev.quikkkk.controller;

import com.dev.quikkkk.dto.request.CreateExerciseRequest;
import com.dev.quikkkk.dto.request.UpdateExerciseRequest;
import com.dev.quikkkk.dto.response.ExerciseResponse;
import com.dev.quikkkk.dto.response.MessageResponse;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;
import com.dev.quikkkk.service.IExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final IExerciseService exerciseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> createExercise(@Valid @RequestBody CreateExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.createExercise(request));
    }

    @GetMapping("/{exercise-id}")
    public ResponseEntity<ExerciseResponse> findExerciseById(@PathVariable("exercise-id") String exerciseId) {
        return ResponseEntity.ok(exerciseService.findExerciseById(exerciseId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ExerciseResponse>> findAllExercises(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(exerciseService.findAllExercises(page, size, search));
    }

    @GetMapping("/active")
    public ResponseEntity<PageResponse<ExerciseResponse>> findAllActiveExercises(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(exerciseService.findAllActiveExercises(page, size));
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<PageResponse<ExerciseResponse>> findAllByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable ExerciseCategory category
    ) {
        return ResponseEntity.ok(exerciseService.findAllExercisesByCategory(category, page, size));
    }

    @GetMapping("/by-muscle-group/{muscle-group}")
    public ResponseEntity<PageResponse<ExerciseResponse>> findAllByMuscleGroup(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("muscle-group") MuscleGroup muscleGroup
    ) {
        return ResponseEntity.ok(exerciseService.findAllExercisesByMuscleGroup(muscleGroup, page, size));
    }

    @PutMapping("/{exercise-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable("exercise-id") String exerciseId,
            @Valid @RequestBody UpdateExerciseRequest request
    ) {
        return ResponseEntity.ok(exerciseService.updateExercise(exerciseId, request));
    }

    @PatchMapping("/{exercise-id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> activateExercise(@PathVariable("exercise-id") String exerciseId) {
        return ResponseEntity.ok(exerciseService.activateExercise(exerciseId));
    }

    @PatchMapping("/{exercise-id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deactivateExercise(@PathVariable("exercise-id") String exerciseId) {
        return ResponseEntity.ok(exerciseService.deactivateExercise(exerciseId));
    }

    @DeleteMapping("/{exercise-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteExercise(@PathVariable("exercise-id") String exerciseId) {
        return ResponseEntity.ok(exerciseService.deleteExercise(exerciseId));
    }
}
