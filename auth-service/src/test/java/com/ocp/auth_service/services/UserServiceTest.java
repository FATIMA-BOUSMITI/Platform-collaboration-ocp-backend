package com.ocp.auth_service.services;

import com.ocp.auth_service.dto.request.CreateUserRequest;
import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.mappers.UserMapper;
import com.ocp.auth_service.Repository.RoleRepository;
import com.ocp.auth_service.Repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
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

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUser_whenEmailDoesNotExist() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("fatima@ocp.com");
        request.setPassword("motdepasse123");
        when(userCredentialRepository.existsByEmail("fatima@ocp.com")).thenReturn(false);
        when(passwordEncoder.encode("motdepasse123")).thenReturn("hash_simule");
        UserCredential savedUser =  UserCredential.builder()
                .id(UUID.randomUUID())
                .email("fatima@ocp.com")
                .passwordHash("hash_simule")
                .enabled(true)
                .build();

        when(userCredentialRepository.save(ArgumentMatchers.any(UserCredential.class))).thenReturn(savedUser);
        UserResponse expectedResponse = new UserResponse(
                savedUser.getId(), "fatima@ocp.com", true, false, null, null, Set.of()
        );
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);
        UserResponse result = userService.createUser(request);
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("fatima@ocp.com");
        assertThat(result.getEnabled()).isTrue();
    }
}
