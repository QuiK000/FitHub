package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.PhotoAngle;
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
public class ProgressPhotoResponse {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime photoDate;
    private String photoUrl;
    private PhotoAngle angle;
    private String notes;
    private BodyMeasurementShortResponse measurement;
}
