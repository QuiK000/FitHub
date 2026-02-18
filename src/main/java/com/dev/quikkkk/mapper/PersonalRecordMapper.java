package com.dev.quikkkk.mapper;

import com.dev.quikkkk.dto.request.CreatePersonalRecordRequest;
import com.dev.quikkkk.dto.response.ExerciseShortResponse;
import com.dev.quikkkk.dto.response.PersonalRecordResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.PersonalRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PersonalRecordMapper {
    public PersonalRecord toEntity(CreatePersonalRecordRequest request, Exercise exercise, ClientProfile client) {
        return PersonalRecord.builder()
                .exercise(exercise)
                .client(client)
                .recordType(request.getRecordType())
                .value(request.getValue())
                .unit(request.getUnit())
                .notes(request.getNotes())
                .videoUrl(request.getVideoUrl())
                .recordDate(LocalDateTime.now())
                .createdBy(client.getId())
                .build();
    }

    public PersonalRecordResponse toResponse(PersonalRecord record) {
        return PersonalRecordResponse.builder()
                .id(record.getId())
                .exercise(
                        ExerciseShortResponse.builder()
                                .exerciseId(record.getExercise().getId())
                                .name(record.getExercise().getName())
                                .imageUrl(record.getExercise().getImageUrl())
                                .primaryMuscleGroup(record.getExercise().getPrimaryMuscleGroup())
                                .category(record.getExercise().getCategory())
                                .build()
                )
                .recordType(record.getRecordType())
                .value(record.getValue())
                .unit(record.getUnit())
                .recordDate(record.getRecordDate())
                .previousRecord(record.getPreviousRecord())
                .improvement(0.0)
                .notes(record.getNotes())
                .videoUrl(record.getVideoUrl())
                .build();
    }
}
