package com.dev.quikkkk.service;

import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.request.ExtendMembershipRequest;
import com.dev.quikkkk.dto.response.MembershipResponse;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.fixtures.TestFixtures;
import com.dev.quikkkk.mapper.MembershipMapper;
import com.dev.quikkkk.repository.IClientProfileRepository;
import com.dev.quikkkk.repository.IMembershipRepository;
import com.dev.quikkkk.service.impl.MembershipServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.dev.quikkkk.enums.ErrorCode.CLIENT_ALREADY_HAS_ACTIVE_MEMBERSHIP;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_TYPE_NOT_EXTENDABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_TYPE_NOT_FREEZABLE;
import static com.dev.quikkkk.enums.ErrorCode.MEMBERSHIP_VISITS_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MembershipService Tests")
class MembershipServiceImplTest {

    @Mock
    private IMembershipRepository membershipRepository;
    @Mock
    private IClientProfileRepository clientProfileRepository;
    @Mock
    private MembershipMapper membershipMapper;

    @InjectMocks
    private MembershipServiceImpl membershipService;

    @Test
    @DisplayName("Should create monthly membership successfully")
    void createMembership_WithMonthlyType_CreatesMembership() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        CreateMembershipRequest request = TestFixtures.createMembershipRequest(
                client.getId(),
                MembershipType.MONTHLY
        );

        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.MONTHLY,
                MembershipStatus.CREATED
        );

        MembershipResponse expectedResponse = MembershipResponse.builder()
                .id(membership.getId())
                .type(MembershipType.MONTHLY)
                .status(MembershipStatus.CREATED)
                .build();

        when(clientProfileRepository.findById(request.getClientId())).thenReturn(Optional.of(client));
        when(membershipMapper.toEntity(any(), any(), any(), any(), any(), any(), any())).thenReturn(membership);
        when(membershipRepository.save(membership)).thenReturn(membership);
        when(membershipMapper.toResponse(membership)).thenReturn(expectedResponse);

        // when
        MembershipResponse response = membershipService.createMembership(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(MembershipType.MONTHLY);
        assertThat(response.getStatus()).isEqualTo(MembershipStatus.CREATED);
        verify(membershipRepository).save(membership);
    }

    @Test
    @DisplayName("Should throw exception when creating visits membership without visits limit")
    void createMembership_WithVisitsTypeNoLimit_ThrowsBusinessException() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        CreateMembershipRequest request = CreateMembershipRequest.builder()
                .clientId(client.getId())
                .type(MembershipType.VISITS)
                .visitsLimit(null)
                .build();

        when(clientProfileRepository.findById(request.getClientId())).thenReturn(Optional.of(client));

        // when & then
        assertThatThrownBy(() -> membershipService.createMembership(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBERSHIP_VISITS_REQUIRED);
    }

    @Test
    @DisplayName("Should activate membership successfully")
    void activateMembership_WithValidId_ActivatesMembership() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.MONTHLY,
                MembershipStatus.CREATED
        );

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));
        when(membershipRepository.existsByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE))
                .thenReturn(false);
        when(membershipRepository.save(membership)).thenReturn(membership);
        when(membershipMapper.toResponse(membership)).thenReturn(MembershipResponse.builder().build());

        // when
        MembershipResponse response = membershipService.activateMembership(membership.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
        assertThat(membership.getStartDate()).isNotNull();
        assertThat(membership.getEndDate()).isNotNull();
        verify(membershipRepository).save(membership);
    }

    @Test
    @DisplayName("Should throw exception when client already has active membership")
    void activateMembership_WhenClientHasActive_ThrowsBusinessException() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.MONTHLY,
                MembershipStatus.CREATED
        );

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));
        when(membershipRepository.existsByClientIdAndStatus(client.getId(), MembershipStatus.ACTIVE))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> membershipService.activateMembership(membership.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CLIENT_ALREADY_HAS_ACTIVE_MEMBERSHIP);
    }

    @Test
    @DisplayName("Should freeze membership successfully")
    void freezeMembership_WithActiveMonthly_FreezesMembership() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.MONTHLY,
                MembershipStatus.ACTIVE
        );

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));
        when(membershipRepository.save(membership)).thenReturn(membership);
        when(membershipMapper.toResponse(membership)).thenReturn(MembershipResponse.builder().build());

        // when
        MembershipResponse response = membershipService.freezeMembership(membership.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.FROZEN);
        assertThat(membership.getFreezeDate()).isNotNull();
        verify(membershipRepository).save(membership);
    }

    @Test
    @DisplayName("Should throw exception when freezing visits membership")
    void freezeMembership_WithVisitsType_ThrowsBusinessException() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.VISITS,
                MembershipStatus.ACTIVE
        );

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        // when & then
        assertThatThrownBy(() -> membershipService.freezeMembership(membership.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBERSHIP_TYPE_NOT_FREEZABLE);
    }

    @Test
    @DisplayName("Should unfreeze membership and extend end date")
    void unfreezeMembership_WithFrozenMembership_UnfreezesAndExtendsDate() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.MONTHLY,
                MembershipStatus.FROZEN
        );

        LocalDateTime freezeDate = LocalDateTime.now().minusDays(5);
        LocalDateTime originalEndDate = LocalDateTime.now().plusMonths(1);
        membership.setFreezeDate(freezeDate);
        membership.setEndDate(originalEndDate);

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));
        when(membershipRepository.save(membership)).thenReturn(membership);
        when(membershipMapper.toResponse(membership)).thenReturn(MembershipResponse.builder().build());

        // when
        MembershipResponse response = membershipService.unfreezeMembership(membership.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
        assertThat(membership.getFreezeDate()).isNull();
        assertThat(membership.getEndDate()).isAfter(originalEndDate);
    }

    @Test
    @DisplayName("Should extend membership successfully")
    void extendMembership_WithValidMonths_ExtendsMembership() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.MONTHLY,
                MembershipStatus.ACTIVE
        );

        LocalDateTime originalEndDate = LocalDateTime.now().plusMonths(1);
        membership.setEndDate(originalEndDate);

        ExtendMembershipRequest request = ExtendMembershipRequest.builder()
                .months(3)
                .build();

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));
        when(membershipRepository.save(membership)).thenReturn(membership);
        when(membershipMapper.toResponse(membership)).thenReturn(MembershipResponse.builder().build());

        // when
        MembershipResponse response = membershipService.extendMembership(membership.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(membership.getEndDate()).isEqualTo(originalEndDate.plusMonths(3));
        verify(membershipRepository).save(membership);
    }

    @Test
    @DisplayName("Should throw exception when extending visits membership")
    void extendMembership_WithVisitsType_ThrowsBusinessException() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.VISITS,
                MembershipStatus.ACTIVE
        );

        ExtendMembershipRequest request = ExtendMembershipRequest.builder()
                .months(1)
                .build();

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));

        // when & then
        assertThatThrownBy(() -> membershipService.extendMembership(membership.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MEMBERSHIP_TYPE_NOT_EXTENDABLE);
    }

    @Test
    @DisplayName("Should cancel membership successfully")
    void cancelMembership_WithActiveMembership_CancelsMembership() {
        // given
        User user = TestFixtures.createClientUser();
        ClientProfile client = TestFixtures.createClientProfile(user);
        Membership membership = TestFixtures.createMembership(
                client,
                MembershipType.MONTHLY,
                MembershipStatus.ACTIVE
        );

        when(membershipRepository.findById(membership.getId())).thenReturn(Optional.of(membership));
        when(membershipRepository.save(membership)).thenReturn(membership);
        when(membershipMapper.toResponse(membership)).thenReturn(MembershipResponse.builder().build());

        // when
        MembershipResponse response = membershipService.cancelMembership(membership.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.CANCELLED);
        assertThat(membership.getEndDate()).isNotNull();
        verify(membershipRepository).save(membership);
    }
}
