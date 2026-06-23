package com.dev.quikkkk.service;

import com.dev.quikkkk.core.exception.BusinessException;
import com.dev.quikkkk.modules.user.dto.request.CreateSpecializationRequest;
import com.dev.quikkkk.modules.user.dto.request.UpdateSpecializationRequest;
import com.dev.quikkkk.modules.user.dto.response.SpecializationResponse;
import com.dev.quikkkk.modules.user.entity.Specialization;
import com.dev.quikkkk.modules.user.mapper.SpecializationMapper;
import com.dev.quikkkk.modules.user.repository.ISpecializationRepository;
import com.dev.quikkkk.modules.user.service.impl.SpecializationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.dev.quikkkk.core.enums.ErrorCode.SPECIALIZATION_ALREADY_EXISTS;
import static com.dev.quikkkk.core.enums.ErrorCode.SPECIALIZATION_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpecializationService Tests")
class SpecializationServiceImplTest {

    @Mock
    private ISpecializationRepository specializationRepository;
    @Mock
    private SpecializationMapper specializationMapper;

    @InjectMocks
    private SpecializationServiceImpl specializationService;

    @Test
    @DisplayName("Should create specialization successfully")
    void create_WithValidRequest_ReturnsResponse() {
        CreateSpecializationRequest request = CreateSpecializationRequest.builder()
                .name("CrossFit")
                .description("High intensity functional training")
                .build();
        Specialization spec = Specialization.builder().id(UUID.randomUUID().toString()).name("CrossFit").active(true).build();
        SpecializationResponse expected = SpecializationResponse.builder().id(spec.getId()).name("CrossFit").build();

        when(specializationRepository.existsByNameIgnoreCase("CrossFit")).thenReturn(false);
        when(specializationMapper.toEntity(request)).thenReturn(spec);
        when(specializationMapper.toResponse(spec)).thenReturn(expected);

        SpecializationResponse response = specializationService.create(request);

        assertThat(response).isNotNull();
        verify(specializationRepository).save(spec);
    }

    @Test
    @DisplayName("Should throw exception when specialization already exists")
    void create_WithDuplicateName_ThrowsBusinessException() {
        CreateSpecializationRequest request = CreateSpecializationRequest.builder()
                .name("CrossFit")
                .build();

        when(specializationRepository.existsByNameIgnoreCase("CrossFit")).thenReturn(true);

        assertThatThrownBy(() -> specializationService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SPECIALIZATION_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Should update specialization")
    void update_WithValidId_ReturnsResponse() {
        String specId = UUID.randomUUID().toString();
        Specialization spec = Specialization.builder().id(specId).name("Old Name").active(true).build();
        UpdateSpecializationRequest request = UpdateSpecializationRequest.builder().name("New Name").build();
        SpecializationResponse expected = SpecializationResponse.builder().id(specId).name("New Name").build();

        when(specializationRepository.findById(specId)).thenReturn(Optional.of(spec));
        when(specializationMapper.toResponse(spec)).thenReturn(expected);

        SpecializationResponse response = specializationService.update(specId, request);

        assertThat(response).isNotNull();
        verify(specializationRepository).save(spec);
    }

    @Test
    @DisplayName("Should throw exception when specialization not found for update")
    void update_WithNonExistingId_ThrowsBusinessException() {
        String specId = UUID.randomUUID().toString();
        when(specializationRepository.findById(specId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationService.update(specId,
                UpdateSpecializationRequest.builder().build()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SPECIALIZATION_NOT_FOUND);
    }

    @Test
    @DisplayName("Should disable specialization")
    void disable_WithActiveSpec_DisablesIt() {
        String specId = UUID.randomUUID().toString();
        Specialization spec = Specialization.builder().id(specId).active(true).build();
        SpecializationResponse expected = SpecializationResponse.builder().id(specId).name("Disabled").build();

        when(specializationRepository.findById(specId)).thenReturn(Optional.of(spec));
        when(specializationMapper.toResponse(spec)).thenReturn(expected);

        SpecializationResponse response = specializationService.disable(specId);

        assertThat(response).isNotNull();
        assertThat(spec.isActive()).isFalse();
        verify(specializationRepository).save(spec);
    }

    @Test
    @DisplayName("Should throw exception when disabling non-existing specialization")
    void disable_WithNonExistingId_ThrowsBusinessException() {
        String specId = UUID.randomUUID().toString();
        when(specializationRepository.findById(specId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationService.disable(specId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SPECIALIZATION_NOT_FOUND);
    }
}
