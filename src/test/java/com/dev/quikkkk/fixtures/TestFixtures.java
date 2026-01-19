package com.dev.quikkkk.fixtures;

import com.dev.quikkkk.dto.request.CreateClientProfileRequest;
import com.dev.quikkkk.dto.request.CreateMembershipRequest;
import com.dev.quikkkk.dto.request.CreateTrainerProfileRequest;
import com.dev.quikkkk.dto.request.CreateTrainingSessionRequest;
import com.dev.quikkkk.dto.request.LoginRequest;
import com.dev.quikkkk.dto.request.RegistrationRequest;
import com.dev.quikkkk.entity.ClientProfile;
import com.dev.quikkkk.entity.Exercise;
import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.entity.Payment;
import com.dev.quikkkk.entity.Role;
import com.dev.quikkkk.entity.Specialization;
import com.dev.quikkkk.entity.TrainerProfile;
import com.dev.quikkkk.entity.TrainingSession;
import com.dev.quikkkk.entity.User;
import com.dev.quikkkk.enums.ExerciseCategory;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.enums.MembershipType;
import com.dev.quikkkk.enums.MuscleGroup;
import com.dev.quikkkk.enums.PaymentStatus;
import com.dev.quikkkk.enums.TrainingStatus;
import com.dev.quikkkk.enums.TrainingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

public class TestFixtures {

    // User Fixtures
    public static User createUser(String email, String password, Set<Role> roles) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .password(password)
                .enabled(true)
                .roles(roles)
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static User createClientUser() {
        Role clientRole = Role.builder()
                .id(UUID.randomUUID().toString())
                .name("CLIENT")
                .build();
        return createUser("client@test.com", "encodedPassword", Set.of(clientRole));
    }

    public static User createTrainerUser() {
        Role trainerRole = Role.builder()
                .id(UUID.randomUUID().toString())
                .name("TRAINER")
                .build();
        return createUser("trainer@test.com", "encodedPassword", Set.of(trainerRole));
    }

    // ClientProfile Fixtures
    public static ClientProfile createClientProfile(User user) {
        return ClientProfile.builder()
                .id(UUID.randomUUID().toString())
                .firstname("John")
                .lastname("Doe")
                .phone("+380501234567")
                .birthdate(LocalDateTime.of(1990, 1, 1, 0, 0))
                .height(180.0)
                .weight(75.0)
                .active(true)
                .user(user)
                .createdBy(user.getId())
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static CreateClientProfileRequest createClientProfileRequest() {
        return CreateClientProfileRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .phone("+380501234567")
                .birthdate(LocalDateTime.of(1990, 1, 1, 0, 0))
                .height(180.0)
                .weight(75.0)
                .build();
    }

    // TrainerProfile Fixtures
    public static TrainerProfile createTrainerProfile(User user, Set<Specialization> specializations) {
        return TrainerProfile.builder()
                .id(UUID.randomUUID().toString())
                .firstname("Jane")
                .lastname("Smith")
                .specialization(specializations)
                .experienceYears(5)
                .description("Experienced trainer")
                .active(true)
                .user(user)
                .createdBy(user.getId())
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static CreateTrainerProfileRequest createTrainerProfileRequest(Set<String> specializationIds) {
        return CreateTrainerProfileRequest.builder()
                .firstname("Jane")
                .lastname("Smith")
                .specializationIds(specializationIds)
                .experienceYears(5)
                .description("Experienced trainer")
                .build();
    }

    // Specialization Fixtures
    public static Specialization createSpecialization(String name) {
        return Specialization.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .description(name + " description")
                .active(true)
                .build();
    }

    // Membership Fixtures
    public static Membership createMembership(ClientProfile client, MembershipType type, MembershipStatus status) {
        return Membership.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .status(status)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .visitsLeft(type == MembershipType.VISITS ? 10 : null)
                .durationMonths(type != MembershipType.VISITS ? 1 : null)
                .client(client)
                .createdBy("ADMIN")
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static CreateMembershipRequest createMembershipRequest(String clientId, MembershipType type) {
        return CreateMembershipRequest.builder()
                .clientId(clientId)
                .type(type)
                .durationMonths(type != MembershipType.VISITS ? 1 : null)
                .visitsLimit(type == MembershipType.VISITS ? 10 : null)
                .build();
    }

    // TrainingSession Fixtures
    public static TrainingSession createTrainingSession(TrainerProfile trainer, TrainingType type) {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        return TrainingSession.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .maxParticipants(type == TrainingType.PERSONAL ? 1 : 10)
                .status(TrainingStatus.SCHEDULED)
                .trainer(trainer)
                .createdBy(trainer.getId())
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static CreateTrainingSessionRequest createTrainingSessionRequest(TrainingType type) {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        return CreateTrainingSessionRequest.builder()
                .type(type)
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .maxParticipants(type == TrainingType.PERSONAL ? 1 : 10)
                .build();
    }

    // Payment Fixtures
    public static Payment createPayment(ClientProfile client, Membership membership) {
        return Payment.builder()
                .id(UUID.randomUUID().toString())
                .amount(new BigDecimal("100.00"))
                .currency(Currency.getInstance("USD"))
                .status(PaymentStatus.PAID)
                .paymentDate(LocalDateTime.now())
                .client(client)
                .membership(membership)
                .createdBy(client.getId())
                .createdDate(LocalDateTime.now())
                .build();
    }

    // Exercise Fixtures
    public static Exercise createExercise(String name, ExerciseCategory category) {
        return Exercise.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .description("Description for " + name)
                .category(category)
                .primaryMuscleGroup(MuscleGroup.CHEST)
                .active(true)
                .createdBy("ADMIN")
                .createdDate(LocalDateTime.now())
                .build();
    }

    // Authentication Fixtures
    public static LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public static RegistrationRequest createRegistrationRequest() {
        return RegistrationRequest.builder()
                .email("newuser@test.com")
                .password("StrongP@ss123")
                .confirmPassword("StrongP@ss123")
                .build();
    }
}
