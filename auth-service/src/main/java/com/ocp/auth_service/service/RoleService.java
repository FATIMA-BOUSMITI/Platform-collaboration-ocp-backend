package com.ocp.auth_service.service;

import com.ocp.auth_service.dto.request.UpdateRolePermissionsRequest;
import com.ocp.auth_service.dto.response.RoleResponse;
import com.ocp.auth_service.entity.Permission;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.exception.PermissionNotFoundException;
import com.ocp.auth_service.exception.RoleNotFoundException;
import com.ocp.auth_service.mapper.RoleMapper;
import com.ocp.auth_service.repository.PermissionRepository;
import com.ocp.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id.toString()));
        return roleMapper.toResponse(role);
    }

    @Transactional
    public RoleResponse updateRolePermissions(UUID roleId, UpdateRolePermissionsRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId.toString()));

        Set<Permission> permissions = request.getPermissionIds().stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new PermissionNotFoundException(permissionId.toString())))
                .collect(Collectors.toSet());

        role.setPermissions(permissions);

        Role updatedRole = roleRepository.save(role);
        return roleMapper.toResponse(updatedRole);
    }
}



