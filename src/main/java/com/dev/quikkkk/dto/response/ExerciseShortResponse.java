package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;
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
