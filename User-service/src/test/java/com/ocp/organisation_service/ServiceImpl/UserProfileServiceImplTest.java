package com.ocp.organisation_service.ServiceImpl;

import com.ocp.organisation_service.client.AuthClient;
import com.ocp.organisation_service.dto.request.CreateCredentialRequest;
import com.ocp.organisation_service.dto.request.UserCreateRequestDTO;
import com.ocp.organisation_service.dto.response.AuthUserResponse;
import com.ocp.organisation_service.dto.response.AuthUserRoleAssignmentResponse;
import com.ocp.organisation_service.dto.response.UserResponseDTO;
import com.ocp.organisation_service.entity.Departement;
import com.ocp.organisation_service.entity.UserProfile;
import com.ocp.organisation_service.mappers.UserMapper;
import com.ocp.organisation_service.repository.DepartementRepository;
import com.ocp.organisation_service.repository.TeamMemberRepository;
import com.ocp.organisation_service.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Test
    void createUser_shouldCreateProfileAndSyncAuth() {
        UUID departmentId = UUID.randomUUID();
        UUID authUserId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        UserCreateRequestDTO request = new UserCreateRequestDTO();
        request.setEmail("fatima@ocp.com");
        request.setFirstName("Fatima");
        request.setLastName("Bous");
        request.setDepartement(departmentId);
        request.setRoleId(UUID.randomUUID());

        Departement department = new Departement();
        department.setId(departmentId);
        department.setName("IT");

        UserProfile profile = new UserProfile();
        profile.setId(profileId);
        profile.setEmail("fatima@ocp.com");
        profile.setFirstName("Fatima");
        profile.setLastName("Bous");
        profile.setDepartement(department);
        profile.setAuthUserId(authUserId);

        AuthUserResponse authUserResponse = new AuthUserResponse();
        authUserResponse.setUserId(authUserId);
        authUserResponse.setEmail("fatima@ocp.com");
        authUserResponse.setEnabled(true);

        UserResponseDTO expectedResponse = UserResponseDTO.builder()
                .id(profileId)
                .userId(authUserId)
                .firstName("Fatima")
                .lastName("Bous")
                .email("fatima@ocp.com")
                .departement(null)
                .build();

        when(userProfileRepository.existsByEmail("fatima@ocp.com")).thenReturn(false);
        when(departementRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(authClient.createUser(any(CreateCredentialRequest.class))).thenReturn(authUserResponse);
        when(userMapper.toEntity(request)).thenReturn(profile);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        when(userMapper.toResponse(profile)).thenReturn(expectedResponse);

        UserResponseDTO result = userProfileService.createUser(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("fatima@ocp.com");
        assertThat(profile.getAuthUserId()).isEqualTo(authUserId);
        verify(authClient).createUser(any(CreateCredentialRequest.class));
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void getUserByAuthUserId_shouldReturnNotFoundWhenProfileIsMissing() {
        UUID authUserId = UUID.randomUUID();
        when(userProfileRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getUserByAuthUserId(authUserId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllUsers_shouldFetchRolesInOneAuthRequestAndRepresentEmptyListAsReadyData() {
        UUID linkedAuthUserId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UserProfile linkedProfile = new UserProfile();
        linkedProfile.setId(UUID.randomUUID());
        linkedProfile.setAuthUserId(linkedAuthUserId);
        linkedProfile.setFirstName("QA");
        linkedProfile.setEmail("qa@example.invalid");

        UserProfile profileWithoutAuthAccount = new UserProfile();
        profileWithoutAuthAccount.setId(UUID.randomUUID());
        profileWithoutAuthAccount.setEmail("legacy@example.invalid");

        when(authClient.getAllUserRoleAssignments()).thenReturn(List.of(
                new AuthUserRoleAssignmentResponse(
                        linkedAuthUserId, roleId, "EMPLOYEE", "Standard user")));
        when(userProfileRepository.findAll())
                .thenReturn(List.of(linkedProfile, profileWithoutAuthAccount));

        List<UserResponseDTO> result = userProfileService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRoles())
                .singleElement()
                .satisfies(role -> {
                    assertThat(role.getId()).isEqualTo(roleId);
                    assertThat(role.getName()).isEqualTo("EMPLOYEE");
                });
        assertThat(result.get(1).getRoles()).isEmpty();
        verify(authClient).getAllUserRoleAssignments();
        verify(authClient, never()).getRolesByUserId(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deleteUser_shouldCleanMembershipAndDeleteCredentialByLinkedProfileUserId() {
        UUID profileId = UUID.randomUUID();
        UUID linkedProfileUserId = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        profile.setId(profileId);
        profile.setAuthUserId(linkedProfileUserId);
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        userProfileService.deleteUser(profileId);

        var deletionOrder = inOrder(teamMemberRepository, authClient, userProfileRepository);
        deletionOrder.verify(teamMemberRepository).deleteByUserId(profileId);
        deletionOrder.verify(authClient).deleteUserByUserId(linkedProfileUserId);
        deletionOrder.verify(userProfileRepository).delete(profile);
    }
}
