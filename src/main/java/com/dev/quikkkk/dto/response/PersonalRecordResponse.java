package com.dev.quikkkk.dto.response;

import com.dev.quikkkk.enums.PersonalRecordUnit;
import com.dev.quikkkk.enums.RecordType;
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
public class PersonalRecordResponse {
    private String id;
    private ExerciseShortResponse exercise;
    private RecordType recordType;
    private Double value;
    private PersonalRecordUnit unit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordDate;
    private Double previousRecord;
    private Double improvement;
    private String notes;
    private String videoUrl;
}
