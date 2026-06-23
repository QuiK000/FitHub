package com.dev.quikkkk.service;

import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.progress.dto.request.CreatePersonalRecordRequest;
import com.dev.quikkkk.modules.progress.dto.response.PersonalRecordResponse;
import com.dev.quikkkk.modules.progress.entity.PersonalRecord;
import com.dev.quikkkk.modules.progress.enums.RecordType;
import com.dev.quikkkk.modules.progress.mapper.PersonalRecordMapper;
import com.dev.quikkkk.modules.progress.repository.IPersonalRecordRepository;
import com.dev.quikkkk.modules.progress.service.impl.PersonalRecordServiceImpl;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import com.dev.quikkkk.modules.workout.entity.Exercise;
import com.dev.quikkkk.modules.workout.service.IExerciseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.FORBIDDEN_ACCESS;
import static com.dev.quikkkk.core.enums.ErrorCode.NOT_A_NEW_RECORD;
import static com.dev.quikkkk.core.enums.ErrorCode.PERSONAL_RECORD_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonalRecordService Tests")
class PersonalRecordServiceImplTest {

    @Mock
    private IPersonalRecordRepository personalRecordRepository;
    @Mock
    private IExerciseService exerciseService;
    @Mock
    private ClientProfileUtils clientProfileUtils;
    @Mock
    private PersonalRecordMapper personalRecordMapper;

    @InjectMocks
    private PersonalRecordServiceImpl personalRecordService;

    @Test
    @DisplayName("Should create personal record when no previous best exists")
    void createPersonalRecord_WithNoPreviousBest_ReturnsResponse() {
        ClientProfile client = createClient();
        Exercise exercise = Exercise.builder().id(UUID.randomUUID().toString()).active(true).build();
        CreatePersonalRecordRequest request = CreatePersonalRecordRequest.builder()
                .exerciseId(exercise.getId())
                .recordType(RecordType.MAX_WEIGHT)
                .value(100.0)
                .build();
        PersonalRecord record = PersonalRecord.builder().id(UUID.randomUUID().toString()).client(client).exercise(exercise).build();
        PersonalRecordResponse expected = PersonalRecordResponse.builder().id(record.getId()).build();

        when(exerciseService.getActiveExerciseEntity(exercise.getId())).thenReturn(exercise);
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(personalRecordRepository.findCurrentBest(client.getId(), exercise.getId(), RecordType.MAX_WEIGHT))
                .thenReturn(Optional.empty());
        when(personalRecordMapper.toEntity(request, exercise, client)).thenReturn(record);
        when(personalRecordRepository.save(record)).thenReturn(record);
        when(personalRecordMapper.toResponse(record)).thenReturn(expected);

        PersonalRecordResponse response = personalRecordService.createPersonalRecord(request);

        assertThat(response).isNotNull();
        verify(personalRecordRepository).save(record);
    }

    @Test
    @DisplayName("Should create personal record when new value is better than current best")
    void createPersonalRecord_WithBetterValue_ReturnsResponse() {
        ClientProfile client = createClient();
        Exercise exercise = Exercise.builder().id(UUID.randomUUID().toString()).active(true).build();
        PersonalRecord currentBest = PersonalRecord.builder().id(UUID.randomUUID().toString()).value(80.0).build();
        CreatePersonalRecordRequest request = CreatePersonalRecordRequest.builder()
                .exerciseId(exercise.getId())
                .recordType(RecordType.MAX_WEIGHT)
                .value(100.0)
                .build();
        PersonalRecord newRecord = PersonalRecord.builder().id(UUID.randomUUID().toString()).client(client).exercise(exercise).value(100.0).build();
        PersonalRecordResponse expected = PersonalRecordResponse.builder().id(newRecord.getId()).build();

        when(exerciseService.getActiveExerciseEntity(exercise.getId())).thenReturn(exercise);
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(personalRecordRepository.findCurrentBest(client.getId(), exercise.getId(), RecordType.MAX_WEIGHT))
                .thenReturn(Optional.of(currentBest));
        when(personalRecordMapper.toEntity(request, exercise, client)).thenReturn(newRecord);
        when(personalRecordRepository.save(newRecord)).thenReturn(newRecord);
        when(personalRecordMapper.toResponse(newRecord)).thenReturn(expected);

        PersonalRecordResponse response = personalRecordService.createPersonalRecord(request);

        assertThat(response).isNotNull();
        verify(personalRecordRepository).disableOldBests(client.getId(), exercise.getId(), RecordType.MAX_WEIGHT);
    }

    @Test
    @DisplayName("Should throw exception when new value is not better than current best")
    void createPersonalRecord_WithWorseValue_ThrowsBusinessException() {
        ClientProfile client = createClient();
        Exercise exercise = Exercise.builder().id(UUID.randomUUID().toString()).active(true).build();
        PersonalRecord currentBest = PersonalRecord.builder().id(UUID.randomUUID().toString()).value(100.0).build();
        CreatePersonalRecordRequest request = CreatePersonalRecordRequest.builder()
                .exerciseId(exercise.getId())
                .recordType(RecordType.MAX_WEIGHT)
                .value(80.0)
                .build();

        when(exerciseService.getActiveExerciseEntity(exercise.getId())).thenReturn(exercise);
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(personalRecordRepository.findCurrentBest(client.getId(), exercise.getId(), RecordType.MAX_WEIGHT))
                .thenReturn(Optional.of(currentBest));

        assertThatThrownBy(() -> personalRecordService.createPersonalRecord(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", NOT_A_NEW_RECORD);
    }

    @Test
    @DisplayName("Should get personal record by id")
    void getPersonalRecordById_WithValidId_ReturnsResponse() {
        ClientProfile client = createClient();
        String recordId = UUID.randomUUID().toString();
        PersonalRecord record = PersonalRecord.builder().id(recordId).client(client).build();
        PersonalRecordResponse expected = PersonalRecordResponse.builder().id(recordId).build();

        when(personalRecordRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(personalRecordMapper.toResponse(record)).thenReturn(expected);

        PersonalRecordResponse response = personalRecordService.getPersonalRecordById(recordId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(recordId);
    }

    @Test
    @DisplayName("Should throw exception when personal record not found")
    void getPersonalRecordById_WithNonExistingId_ThrowsBusinessException() {
        ClientProfile client = createClient();
        String recordId = UUID.randomUUID().toString();

        when(personalRecordRepository.findById(recordId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personalRecordService.getPersonalRecordById(recordId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PERSONAL_RECORD_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw exception when accessing another client's record")
    void getPersonalRecordById_WithForbiddenAccess_ThrowsBusinessException() {
        ClientProfile currentClient = createClient();
        ClientProfile otherClient = createClient();
        String recordId = UUID.randomUUID().toString();
        PersonalRecord record = PersonalRecord.builder().id(recordId).client(otherClient).build();

        when(personalRecordRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(currentClient);

        assertThatThrownBy(() -> personalRecordService.getPersonalRecordById(recordId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", FORBIDDEN_ACCESS);
    }

    private ClientProfile createClient() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        return ClientProfile.builder().id(UUID.randomUUID().toString()).user(user).build();
    }
}
