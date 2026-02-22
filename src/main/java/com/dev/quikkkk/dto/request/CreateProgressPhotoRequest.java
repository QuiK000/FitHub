package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.PhotoAngle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProgressPhotoRequest {
    @NotBlank(message = "VALIDATION.PHOTO.URL.NOT_BLANK")
    private String photoUrl;

    @NotNull(message = "VALIDATION.PHOTO.ANGLE.NOT_NULL")
    private PhotoAngle angle;
    private String notes;
    private String measurementId;
}
