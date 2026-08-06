package com.ocp.auth_service.services;

import com.ocp.auth_service.dto.request.CreateRoleRequest;
import com.ocp.auth_service.dto.request.UpdateRolePermissionsRequest;
import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.dto.response.RoleResponse;
import com.ocp.auth_service.dto.response.RoleUserCountResponse;
import com.ocp.auth_service.entity.Permission;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.exception.PermissionNotFoundException;
import com.ocp.auth_service.exception.RoleNotFoundException;
import com.ocp.auth_service.mappers.PermissionMapper;
import com.ocp.auth_service.mappers.RoleMapper;
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
    private final PermissionMapper permissionMapper ;


	@Transactional
	public RoleResponse createRole(CreateRoleRequest request) {
		Role role = new Role();
		role.setName(request.getName());
		role.setDescription(request.getDescription());
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



    @Transactional(readOnly = true)
    public List<RoleUserCountResponse> getUserCountsByRole() {
        return roleRepository.countUsersByRole().stream()
                .map(p -> new RoleUserCountResponse(p.getRoleName(), p.getUserCount()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getRolePermissions( UUID id ){
        Role role =roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id.toString()));

        return role.getPermissions()
                .stream()
                .map(permissionMapper::toResponse)
                .toList();

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
