package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MuscleGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponse {
    private String id;
    private String name;
    private String description;
    private ExerciseCategory category;
    private MuscleGroup primaryMuscleGroup;
    private Set<MuscleGroup> secondaryMuscleGroups;
    private String videoUrl;
    private String imageUrl;
    private String instructions;
    private boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
