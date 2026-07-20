package com.ocp.auth_service.mapper;

import com.ocp.auth_service.dto.response.UserResponse;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(UserCredential user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getEnabled(),
                user.getAccountLocked(),
                user.getLastLogin(),
                user.getCreatedAt(),
                roleNames
        );
    }
}
