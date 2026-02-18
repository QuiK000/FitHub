package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreatePersonalRecordRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.PersonalRecordResponse;

public interface IPersonalRecordService {
    PersonalRecordResponse createPersonalRecord(CreatePersonalRecordRequest request);

    PageResponse<PersonalRecordResponse> getPersonalRecords(int page, int size);

    PersonalRecordResponse getPersonalRecordById(String personalRecordId);

    PageResponse<PersonalRecordResponse> getPersonalRecordsByExerciseId(String exerciseId, int page, int size);

    PageResponse<PersonalRecordResponse> getRecentPersonalRecords(int limit);
}
