package com.ocp.auth_service.service;

import com.ocp.auth_service.dto.request.AssignRoleRequest;
import com.ocp.auth_service.dto.request.CreateUserRequest;
import com.ocp.auth_service.dto.request.UpdateUserRequest;
import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.exception.EmailAlreadyExistsException;
import com.ocp.auth_service.exception.UserNotFoundException;
import com.ocp.auth_service.mapper.UserMapper;
import com.ocp.auth_service.repository.RoleRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserCredentialRepository userCredentialRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final UserMapper userMapper ;
    private final RoleRepository roleRepository ;

    @Transactional
    public UserResponse createUser(CreateUserRequest request){
     if(userCredentialRepository.existsByEmail(request.getEmail())) {
         throw new EmailAlreadyExistsException(request.getEmail());
     }
        UserCredential user= new UserCredential();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setFailedAttempts(0);

        UserCredential savedUser= userCredentialRepository.save(user);

        return userMapper.toResponse(savedUser);

     }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id){

        UserCredential user= userCredentialRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException(id.toString()));


        return userMapper.toResponse(user);

    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(){
        return userCredentialRepository.findAll().stream()
                .map(userMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        UserCredential user = userCredentialRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getAccountLocked() != null) {
            user.setAccountLocked(request.getAccountLocked());
        }

        UserCredential updatedUser = userCredentialRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserResponse assignRoles(AssignRoleRequest request) {
        UserCredential user = userCredentialRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId().toString()));

        Set<Role> roles = request.getRoleIds().stream()
                .map(roleId -> {
                    try {
                        return roleRepository.findById(roleId)
                                .orElseThrow(() -> new RoleNotFoundException(roleId.toString()));
                    } catch (RoleNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        user.getRoles().addAll(roles);

        UserCredential updatedUser = userCredentialRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userCredentialRepository.existsById(id)) {
            throw new UserNotFoundException(id.toString());
        }
        userCredentialRepository.deleteById(id);
    }
}
