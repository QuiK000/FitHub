package com.dev.quikkkk.modules.progress.service;

import com.dev.quikkkk.modules.progress.dto.request.CreatePersonalRecordRequest;
import com.dev.quikkkk.core.dto.PageResponse;
import com.dev.quikkkk.modules.progress.dto.response.PersonalRecordResponse;

public interface IPersonalRecordService {
    PersonalRecordResponse createPersonalRecord(CreatePersonalRecordRequest request);

    PageResponse<PersonalRecordResponse> getPersonalRecords(int page, int size);

    PersonalRecordResponse getPersonalRecordById(String personalRecordId);

    PageResponse<PersonalRecordResponse> getPersonalRecordsByExerciseId(String exerciseId, int page, int size);

    PageResponse<PersonalRecordResponse> getRecentPersonalRecords(int limit);
}
