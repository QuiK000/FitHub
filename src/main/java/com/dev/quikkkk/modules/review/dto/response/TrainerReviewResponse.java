package com.dev.quikkkk.modules.review.dto.response;

import com.dev.quikkkk.modules.user.dto.response.ClientShortResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TrainerReviewResponse {
    private String id;
    private ClientShortResponse reviewer;
    private Integer rating;
    private String comment;
    private Integer professionalismRating;
    private Integer knowledgeRating;
    private Integer communicationRating;
    private Integer motivationRating;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private boolean edited;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editedAt;
}
