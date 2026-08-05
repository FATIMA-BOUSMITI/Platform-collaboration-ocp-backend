package com.ocp.auth_service.controllers;

import com.ocp.auth_service.dto.request.CreateRoleRequest;
import com.ocp.auth_service.dto.request.UpdateRolePermissionsRequest;
import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.dto.response.RoleResponse;
import com.ocp.auth_service.dto.response.RoleUserCountResponse;
import com.ocp.auth_service.services.RoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/roles")
@RestController
@AllArgsConstructor

public class RoleController {
	private final RoleService roleService ;

	@PostMapping
	public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
		RoleResponse response = roleService.createRole(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
		RoleResponse response = roleService.getRoleById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<RoleResponse>> getAllUsers() {
		List<RoleResponse> responses = roleService.getAllRoles();
		return ResponseEntity.ok(responses);
	}


    @GetMapping("/stats")
    public ResponseEntity<List<RoleUserCountResponse>> getRoleStats() {
        return ResponseEntity.ok(roleService.getUserCountsByRole());
    }

    @GetMapping("{id}/permissions")
    public ResponseEntity<List<PermissionResponse>> getRolePermissions(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRolePermissions(id));
    }

    @PutMapping("{id}/permissions")
    public ResponseEntity<RoleResponse> updateRolePermissions( @PathVariable UUID id ,
                                                               @Valid
                                                               @RequestBody UpdateRolePermissionsRequest request){
        return ResponseEntity.ok(roleService.updateRolePermissions(id,request));
    }

}
