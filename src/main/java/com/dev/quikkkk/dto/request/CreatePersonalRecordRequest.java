package com.dev.quikkkk.dto.request;

import com.dev.quikkkk.enums.PersonalRecordUnit;
import com.dev.quikkkk.enums.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreatePersonalRecordRequest {
    @NotBlank(message = "VALIDATION.PR.EXERCISE_ID.NOT_BLANK")
    private String exerciseId;

    @NotNull(message = "VALIDATION.PR.RECORD_TYPE.NOT_NULL")
    private RecordType recordType;

    @Positive(message = "VALIDATION.PR.VALUE.POSITIVE")
    private Double value;

    @NotNull(message = "VALIDATION.PR.UNIT.NOT_NULL")
    private PersonalRecordUnit unit;

    private String notes;
    private String videoUrl;
}
