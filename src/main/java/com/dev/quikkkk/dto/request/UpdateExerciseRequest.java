package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExerciseRequest {
    private String name;
    private String description;
    private ExerciseCategory category;
    private MuscleGroup primaryMuscleGroup;
    private Set<MuscleGroup> secondaryMuscleGroups;

    @Pattern(regexp = "^https?://.*", message = "VALIDATION.EXERCISE.VIDEO_URL.INVALID")
    private String videoUrl;
    private String imageUrl;
    private String instructions;
}
