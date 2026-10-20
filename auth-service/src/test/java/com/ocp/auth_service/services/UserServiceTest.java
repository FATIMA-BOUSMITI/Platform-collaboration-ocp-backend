package com.ocp.auth_service.services;

import com.ocp.auth_service.dto.request.CreateUserRequest;
import com.ocp.auth_service.dto.request.AssignRoleRequest;
import com.ocp.auth_service.dto.response.UserRoleAssignmentResponse;
import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.mappers.UserMapper;
import com.ocp.auth_service.exception.UserDeletionNotAllowedException;
import com.ocp.auth_service.repository.LoginHistoryRepository;
import com.ocp.auth_service.repository.PasswordResetTokenRepository;
import com.ocp.auth_service.repository.RefreshTokenRepository;
import com.ocp.auth_service.repository.RoleRepository;
import com.ocp.auth_service.repository.SessionRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock private com.ocp.auth_service.repository.AdminPreferenceRepository preferences;
    @Mock private com.ocp.auth_service.repository.AdminNoticeRepository notices;
    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUser_whenEmailDoesNotExist() {
        UUID roleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("fatima@ocp.com");
        request.setUserId(userId);
        request.setRoleId(roleId);

        Role role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");

        when(userCredentialRepository.existsByEmail("fatima@ocp.com")).thenReturn(false);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("hash_simule");

        UserCredential savedUser = UserCredential.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .email("fatima@ocp.com")
            .passwordHash("hash_simule")
            .enabled(true)
            .accountLocked(false)
            .role(role)
            .build();

        when(userCredentialRepository.save(ArgumentMatchers.any(UserCredential.class))).thenReturn(savedUser);
        UserResponse expectedResponse = new UserResponse(
            savedUser.getUserId(), "fatima@ocp.com", true, false, null, null, "ADMIN"
        );
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.createUser(request);
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("fatima@ocp.com");
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void getAllUserRoleAssignments_shouldUseSingleRoleAssignmentQuery() {
        List<UserRoleAssignmentResponse> assignments = List.of(
            new UserRoleAssignmentResponse(
                UUID.randomUUID(), UUID.randomUUID(), "EMPLOYEE", "Standard user"));
        when(userCredentialRepository.findAllUserRoleAssignments()).thenReturn(assignments);

        assertThat(userService.getAllUserRoleAssignments()).isEqualTo(assignments);

        verify(userCredentialRepository).findAllUserRoleAssignments();
        verifyNoMoreInteractions(userCredentialRepository);
    }

    @Test
    void assignRole_shouldFindCredentialByLinkedProfileUserId() {
        UUID profileUserId = UUID.randomUUID();
        UUID credentialId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        Role currentRole = new Role();
        currentRole.setName("EMPLOYEE");
        Role updatedRole = new Role();
        updatedRole.setId(roleId);
        updatedRole.setName("MANAGER");
        UserCredential credential = UserCredential.builder()
            .id(credentialId)
            .userId(profileUserId)
            .role(currentRole)
            .build();
        AssignRoleRequest request = new AssignRoleRequest();
        request.setUserId(profileUserId);
        request.setRoleId(roleId);
        UserResponse expectedResponse = new UserResponse(
            profileUserId, "qa.user@example.invalid", true, false, null, null, "MANAGER");

        when(userCredentialRepository.findByUserId(profileUserId))
            .thenReturn(Optional.of(credential));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(updatedRole));
        when(userCredentialRepository.save(credential)).thenReturn(credential);
        when(userMapper.toResponse(credential)).thenReturn(expectedResponse);

        assertThat(userService.assignRole(request)).isSameAs(expectedResponse);
        assertThat(credential.getRole()).isSameAs(updatedRole);
        verify(userCredentialRepository).findByUserId(profileUserId);
        verify(userCredentialRepository, never()).findById(profileUserId);
    }

    @Test
    void deleteUserByUserId_shouldDeleteOnlyThatUsersAuthenticationData() {
        UUID linkedProfileUserId = UUID.randomUUID();
        Role role = new Role();
        role.setName("EMPLOYEE");
        UserCredential credential = UserCredential.builder()
            .id(UUID.randomUUID())
            .userId(linkedProfileUserId)
            .role(role)
            .build();
        when(userCredentialRepository.findByUserId(linkedProfileUserId))
            .thenReturn(Optional.of(credential));

        userService.deleteUserByUserId(linkedProfileUserId);

        var deletionOrder = inOrder(
            sessionRepository,
            refreshTokenRepository,
            passwordResetTokenRepository,
            loginHistoryRepository,
            userCredentialRepository);
        deletionOrder.verify(sessionRepository).deleteByUser(credential);
        deletionOrder.verify(refreshTokenRepository).deleteByUser(credential);
        deletionOrder.verify(passwordResetTokenRepository).deleteByUser(credential);
        deletionOrder.verify(loginHistoryRepository).deleteByUser(credential);
        deletionOrder.verify(userCredentialRepository).delete(credential);
        verify(userCredentialRepository, never()).deleteById(linkedProfileUserId);
    }

    @Test
    void deleteUserByUserId_shouldPreserveAdministratorAccounts() {
        UUID linkedProfileUserId = UUID.randomUUID();
        Role role = new Role();
        role.setName("ADMIN");
        UserCredential administrator = UserCredential.builder()
            .id(UUID.randomUUID())
            .userId(linkedProfileUserId)
            .role(role)
            .build();
        when(userCredentialRepository.findByUserId(linkedProfileUserId))
            .thenReturn(Optional.of(administrator));

        assertThatThrownBy(() -> userService.deleteUserByUserId(linkedProfileUserId))
            .isInstanceOf(UserDeletionNotAllowedException.class);

        verifyNoInteractions(
            sessionRepository,
            refreshTokenRepository,
            passwordResetTokenRepository,
            loginHistoryRepository);
        verify(userCredentialRepository, never()).delete(administrator);
    }

    @Test
    void deleteUserByUserId_shouldPreserveAllAdministratorRoleAliases() {
        for (String roleName : List.of(
            "ROLE_ADMIN",
            "ADMINISTRATOR",
            "ROLE_ADMINISTRATOR",
            "ADMINISTRATEUR",
            "ROLE_ADMINISTRATEUR")) {
            UUID linkedProfileUserId = UUID.randomUUID();
            Role role = new Role();
            role.setName(roleName);
            UserCredential administrator = UserCredential.builder()
                .id(UUID.randomUUID())
                .userId(linkedProfileUserId)
                .role(role)
                .build();
            when(userCredentialRepository.findByUserId(linkedProfileUserId))
                .thenReturn(Optional.of(administrator));

            assertThatThrownBy(() -> userService.deleteUserByUserId(linkedProfileUserId))
                .as("role %s must remain protected", roleName)
                .isInstanceOf(UserDeletionNotAllowedException.class);
        }

        verifyNoInteractions(
            sessionRepository,
            refreshTokenRepository,
            passwordResetTokenRepository,
            loginHistoryRepository);
        verify(userCredentialRepository, never())
            .delete(ArgumentMatchers.any(UserCredential.class));
    }
}
