package com.ocp.auth_service.mapper;

import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getDescription()
        );
    }
}