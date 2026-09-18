package com.ocp.organisation_service.ServiceImpl;

import com.ocp.organisation_service.client.AuthClient;
import com.ocp.organisation_service.dto.request.CreateCredentialRequest;
import com.ocp.organisation_service.dto.request.UserCreateRequestDTO;
import com.ocp.organisation_service.dto.response.AuthUserResponse;
import com.ocp.organisation_service.dto.response.UserResponseDTO;
import com.ocp.organisation_service.entity.Departement;
import com.ocp.organisation_service.entity.UserProfile;
import com.ocp.organisation_service.mappers.UserMapper;
import com.ocp.organisation_service.repository.DepartementRepository;
import com.ocp.organisation_service.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private DepartementRepository departementRepository;

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
}
