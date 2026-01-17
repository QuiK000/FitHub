package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class CreateExerciseRequest {
    @NotBlank(message = "VALIDATION.EXERCISE.NAME.NOT_BLANK")
    @Size(max = 200, message = "VALIDATION.EXERCISE.NAME.SIZE")
    private String name;

    @NotBlank(message = "VALIDATION.EXERCISE.DESCRIPTION.NOT_BLANK")
    private String description;

    @NotNull(message = "VALIDATION.EXERCISE.CATEGORY.NOT_NULL")
    private ExerciseCategory category;

    @NotNull(message = "VALIDATION.EXERCISE.MUSCLE_GROUP.NOT_NULL")
    private MuscleGroup primaryMuscleGroup;
    private Set<MuscleGroup> secondaryMuscleGroups;

    @Pattern(regexp = "^https?://.*", message = "VALIDATION.EXERCISE.VIDEO_URL.INVALID")
    private String videoUrl;
    private String imageUrl;
    private String instructions;
}
