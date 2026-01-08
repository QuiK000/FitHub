package com.dev.quikkkk.dto.response;

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
public class TrainerProfileResponse {
    private String firstname;
    private String lastname;
    private Set<String> specialization;
    private int experienceYears;
    private String description;
    private LocalDateTime createdAt;
}
