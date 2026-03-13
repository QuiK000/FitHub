package com.dev.quikkkk.modules.workout.dto.response;

import com.dev.quikkkk.modules.workout.enums.ExerciseCategory;
import com.dev.quikkkk.modules.workout.enums.MuscleGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseShortResponse {
    private String exerciseId;
    private String name;
    private ExerciseCategory category;
    private MuscleGroup primaryMuscleGroup;
    private String imageUrl;
}
