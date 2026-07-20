package com.ocp.auth_service.service;

import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.entity.Permission;
import com.ocp.auth_service.exception.PermissionNotFoundException;
import com.ocp.auth_service.mapper.PermissionMapper;
import com.ocp.auth_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id.toString()));
        return permissionMapper.toResponse(permission);
    }

    @Transactional
    public PermissionResponse createPermission(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        Permission saved = permissionRepository.save(permission);
        return permissionMapper.toResponse(saved);
    }
}