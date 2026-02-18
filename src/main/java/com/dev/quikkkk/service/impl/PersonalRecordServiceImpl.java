package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.dto.request.CreatePersonalRecordRequest;
import com.dev.quikkkk.dto.response.PageResponse;
import com.dev.quikkkk.dto.response.PersonalRecordResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.PersonalRecord;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.mapper.PersonalRecordMapper;
import com.dev.quikkkk.repository.IPersonalRecordRepository;
import com.dev.quikkkk.service.IExerciseService;
import com.dev.quikkkk.service.IPersonalRecordService;
import com.dev.quikkkk.utils.ClientProfileUtils;
import com.dev.quikkkk.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.dev.quikkkk.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.enums.ErrorCode.PERSONAL_RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PersonalRecordServiceImpl implements IPersonalRecordService {
    private final IPersonalRecordRepository personalRecordRepository;
    private final IExerciseService exerciseService;
    private final ClientProfileUtils clientProfileUtils;
    private final PersonalRecordMapper personalRecordMapper;

    @Override
    public PersonalRecordResponse createPersonalRecord(CreatePersonalRecordRequest request) {
        Exercise exercise = exerciseService.getActiveExerciseEntity(request.getExerciseId());
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();

        PersonalRecord record = personalRecordMapper.toEntity(request, exercise, client);
        personalRecordRepository.save(record);

        return personalRecordMapper.toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PersonalRecordResponse> getPersonalRecords(int page, int size) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<PersonalRecord> personalRecordPage = personalRecordRepository.findAllByClientId(client.getId(), pageable);

        return PaginationUtils.toPageResponse(personalRecordPage, personalRecordMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalRecordResponse getPersonalRecordById(String personalRecordId) {
        PersonalRecord record = getEntityByIdAndValidateAccess(personalRecordId);
        return personalRecordMapper.toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PersonalRecordResponse> getPersonalRecordsByExerciseId(String exerciseId, int page, int size) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "createdDate");
        Page<PersonalRecord> personalRecordPage = personalRecordRepository.findPersonalRecordByExerciseIdAndClientId(
                exerciseId,
                client.getId(),
                pageable
        );

        return PaginationUtils.toPageResponse(personalRecordPage, personalRecordMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PersonalRecordResponse> getRecentPersonalRecords(int limit) {
        ClientProfile client = clientProfileUtils.getCurrentClientProfile();
        Pageable pageable = PaginationUtils.createPageRequest(0, limit, "createdDate");
        Page<PersonalRecord> personalRecordPage = personalRecordRepository.findByClientIdOrderByCreatedDateDesc(
                client.getId(),
                pageable
        );

        return PaginationUtils.toPageResponse(personalRecordPage, personalRecordMapper::toResponse);
    }

    private PersonalRecord getEntityByIdAndValidateAccess(String id) {
        PersonalRecord personalRecord = personalRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PERSONAL_RECORD_NOT_FOUND));

        ClientProfile currentClient = clientProfileUtils.getCurrentClientProfile();
        if (!Objects.equals(currentClient.getId(), personalRecord.getClient().getId())) {
            log.warn(
                    "Access denied: User [{}] tried to personal record [{}] owned by [{}]",
                    currentClient.getId(),
                    id,
                    personalRecord.getClient().getId()
            );
            throw new BusinessException(FORBIDDEN_ACCESS);
        }

        return personalRecord;
    }
}
