package com.dev.quikkkk.service;

import com.dev.quikkkk.core.enums.ErrorCode;
import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.progress.dto.request.CreateBodyMeasurementRequest;
import com.dev.quikkkk.modules.progress.dto.response.BodyMeasurementResponse;
import com.dev.quikkkk.modules.progress.entity.BodyMeasurement;
import com.dev.quikkkk.modules.progress.mapper.BodyMeasurementMapper;
import com.dev.quikkkk.modules.progress.repository.IBodyMeasurementRepository;
import com.dev.quikkkk.modules.progress.service.impl.BodyMeasurementServiceImpl;
import com.dev.quikkkk.modules.user.entity.ClientProfile;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.user.utils.ClientProfileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.BODY_MEASUREMENT_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.FORBIDDEN_ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BodyMeasurementService Tests")
class BodyMeasurementServiceImplTest {

    @Mock
    private IBodyMeasurementRepository bodyMeasurementRepository;
    @Mock
    private BodyMeasurementMapper bodyMeasurementMapper;
    @Mock
    private ClientProfileUtils clientProfileUtils;

    @InjectMocks
    private BodyMeasurementServiceImpl bodyMeasurementService;

    @Test
    @DisplayName("Should create body measurement successfully")
    void createBodyMeasurement_WithValidRequest_ReturnsResponse() {
        ClientProfile client = createClient();
        CreateBodyMeasurementRequest request = CreateBodyMeasurementRequest.builder()
                .measurementDate(LocalDateTime.now())
                .weight(75.0)
                .build();
        BodyMeasurement measurement = BodyMeasurement.builder().id(UUID.randomUUID().toString()).client(client).build();
        BodyMeasurementResponse expected = BodyMeasurementResponse.builder().id(measurement.getId()).weight(75.0).build();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(bodyMeasurementMapper.toEntity(request, client)).thenReturn(measurement);
        when(bodyMeasurementMapper.toResponse(measurement)).thenReturn(expected);
        when(bodyMeasurementRepository.save(measurement)).thenReturn(measurement);

        BodyMeasurementResponse response = bodyMeasurementService.createBodyMeasurement(request);

        assertThat(response).isNotNull();
        verify(bodyMeasurementRepository).save(measurement);
    }

    @Test
    @DisplayName("Should get body measurement by id")
    void getBodyMeasurementById_WithValidId_ReturnsResponse() {
        ClientProfile client = createClient();
        String measurementId = UUID.randomUUID().toString();
        BodyMeasurement measurement = BodyMeasurement.builder().id(measurementId).client(client).build();
        BodyMeasurementResponse expected = BodyMeasurementResponse.builder().id(measurementId).build();

        when(bodyMeasurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(bodyMeasurementMapper.toResponse(measurement)).thenReturn(expected);

        BodyMeasurementResponse response = bodyMeasurementService.getBodyMeasurementById(measurementId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(measurementId);
    }

    @Test
    @DisplayName("Should throw exception when measurement not found")
    void getBodyMeasurementById_WithNonExistingId_ThrowsBusinessException() {
        ClientProfile client = createClient();
        String measurementId = UUID.randomUUID().toString();

        when(bodyMeasurementRepository.findById(measurementId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bodyMeasurementService.getBodyMeasurementById(measurementId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BODY_MEASUREMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should throw exception when accessing another client's measurement")
    void getBodyMeasurementById_WithForbiddenAccess_ThrowsBusinessException() {
        ClientProfile currentClient = createClient();
        ClientProfile otherClient = createClient();
        String measurementId = UUID.randomUUID().toString();
        BodyMeasurement measurement = BodyMeasurement.builder().id(measurementId).client(otherClient).build();

        when(bodyMeasurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));
        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(currentClient);

        assertThatThrownBy(() -> bodyMeasurementService.getBodyMeasurementById(measurementId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", FORBIDDEN_ACCESS);
    }

    @Test
    @DisplayName("Should get latest body measurement")
    void getLatestBodyMeasurement_WithExistingData_ReturnsResponse() {
        ClientProfile client = createClient();
        BodyMeasurement measurement = BodyMeasurement.builder().id(UUID.randomUUID().toString()).client(client).build();
        BodyMeasurementResponse expected = BodyMeasurementResponse.builder().id(measurement.getId()).build();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(bodyMeasurementRepository.findFirstByClientIdOrderByMeasurementDateDesc(client.getId()))
                .thenReturn(Optional.of(measurement));
        when(bodyMeasurementMapper.toResponse(measurement)).thenReturn(expected);

        BodyMeasurementResponse response = bodyMeasurementService.getLatestBodyMeasurement();

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when no measurements exist")
    void getLatestBodyMeasurement_WithNoData_ThrowsBusinessException() {
        ClientProfile client = createClient();

        when(clientProfileUtils.getCurrentClientProfile()).thenReturn(client);
        when(bodyMeasurementRepository.findFirstByClientIdOrderByMeasurementDateDesc(client.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bodyMeasurementService.getLatestBodyMeasurement())
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BODY_MEASUREMENT_NOT_FOUND);
    }

    private ClientProfile createClient() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        return ClientProfile.builder().id(UUID.randomUUID().toString()).user(user).build();
    }
}
