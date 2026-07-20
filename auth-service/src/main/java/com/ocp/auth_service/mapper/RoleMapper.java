package com.ocp.auth_service.mapper;

import com.ocp.auth_service.dto.response.RoleResponse;
import com.ocp.auth_service.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }


}
