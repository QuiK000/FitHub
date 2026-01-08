package com.dev.quikkkk.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateSpecializationRequest {
    @NotBlank(message = "VALIDATION.CREATE.SPECIALIZATION.NAME.NOT_BLANK")
    private String name;

    @NotBlank(message = "VALIDATION.CREATE.SPECIALIZATION.DESCRIPTION.NOT_BLANK")
    private String description;
}
