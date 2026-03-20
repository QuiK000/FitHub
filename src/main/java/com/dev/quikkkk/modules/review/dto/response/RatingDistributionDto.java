package com.dev.quikkkk.modules.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RatingDistributionDto {
    private Integer fiveStars;
    private Integer fourStars;
    private Integer threeStars;
    private Integer twoStars;
    private Integer oneStar;
}
