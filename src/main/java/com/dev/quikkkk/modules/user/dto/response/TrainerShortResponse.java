package com.dev.quikkkk.modules.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerShortResponse {
    private String trainerId;
    private String firstname;
    private String lastname;
}
