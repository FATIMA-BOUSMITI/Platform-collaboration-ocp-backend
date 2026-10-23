package com.ocp.auth_service.services;

import com.ocp.auth_service.dto.request.CreateRoleRequest;
import com.ocp.auth_service.dto.request.UpdateRolePermissionsRequest;
import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.dto.response.RoleResponse;
import com.ocp.auth_service.dto.response.RoleUserCountResponse;
import com.ocp.auth_service.entity.Permission;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import com.ocp.auth_service.exception.PermissionNotFoundException;
import com.ocp.auth_service.exception.RoleNotFoundException;
import com.ocp.auth_service.mappers.PermissionMapper;
import com.ocp.auth_service.mappers.RoleMapper;
import com.ocp.auth_service.repository.PermissionRepository;
import com.ocp.auth_service.repository.RoleRepository;
import com.ocp.auth_service.repository.UserCredentialRepository;
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
	private  final UserCredentialRepository  userCredentialRepository;


	@Transactional
	public RoleResponse createRole(CreateRoleRequest request) {
		Role role = new Role();
		String normalized=request.getName().trim().toUpperCase();
 if(!normalized.matches("[A-Z][A-Z0-9_]{1,49}"))throw new IllegalArgumentException("Nom de rôle invalide (2 à 50 caractères, lettres, chiffres et _).");
 if(system(normalized))throw new IllegalArgumentException("Ce nom est réservé à un rôle système.");
 if(roleRepository.existsByName(normalized))throw new IllegalArgumentException("Ce rôle existe déjà.");
 role.setName(normalized);
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

        if(system(role.getName()) && !permissions.containsAll(role.getPermissions()))throw new IllegalArgumentException("Les permissions des rôles système ne peuvent pas être retirées.");
        role.setPermissions(permissions);

        Role updatedRole = roleRepository.save(role);
        return roleMapper.toResponse(updatedRole);
    }
	public RoleResponse getRolesByUserId(UUID userId) {

		UserCredential user = userCredentialRepository
			.findByUserId(userId)
			.orElseThrow(() ->
				new RuntimeException("User not found")
			);

		return roleMapper.toResponse(user.getRole());
	}

    private boolean system(String name){return java.util.Set.of("ADMIN","ADMINISTRATOR","ADMINISTRATEUR","DIRECTOR","MANAGER","EMPLOYEE").contains(name.replace("ROLE_", "").toUpperCase());}
    public record PermissionChange(UUID roleId,java.util.Set<UUID> permissionIds,java.util.Set<UUID> originalPermissionIds){}
    @Transactional
    public void batch(java.util.List<PermissionChange> changes){
      if(changes==null || changes.size()>200 || changes.stream().anyMatch(c->c.roleId()==null||c.permissionIds()==null||c.originalPermissionIds()==null) || changes.stream().map(PermissionChange::roleId).distinct().count()!=changes.size())throw new IllegalArgumentException("Matrice invalide.");
      roleRepository.lockAllRoles();
      for(var change:changes){
       Role role=roleRepository.findById(change.roleId()).orElseThrow();
       var current=role.getPermissions().stream().map(Permission::getId).collect(java.util.stream.Collectors.toSet());
       if(!current.equals(change.originalPermissionIds()))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Les permissions ont été modifiées par un autre administrateur. Rechargez la page.");
       var request=new UpdateRolePermissionsRequest();request.setPermissionIds(change.permissionIds());updateRolePermissions(change.roleId(),request);
      }
    }
    @Transactional
    public RoleResponse editRole(UUID id,CreateRoleRequest request){
      Role role=roleRepository.findById(id).orElseThrow();
      if(system(role.getName()))throw new IllegalArgumentException("Ce rôle système est protégé.");
      String name=request.getName().trim().toUpperCase();
      if(!name.matches("[A-Z][A-Z0-9_]{1,49}") || system(name))throw new IllegalArgumentException("Nom de rôle invalide ou réservé.");
      role.setName(name);role.setDescription(request.getDescription());return roleMapper.toResponse(roleRepository.save(role));
    }
    @Transactional
    public void deleteRole(UUID id){Role role=roleRepository.findById(id).orElseThrow();if(system(role.getName()))throw new IllegalArgumentException("Ce rôle système est protégé.");if(userCredentialRepository.existsByRole_Id(id))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Réaffectez les utilisateurs avant de supprimer ce rôle.");roleRepository.delete(role);}

}
