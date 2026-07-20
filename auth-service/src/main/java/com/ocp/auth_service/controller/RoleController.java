package com.ocp.auth_service.controller;

import com.ocp.auth_service.dto.response.RoleResponse;
import com.ocp.auth_service.service.RoleService;
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
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody String name, String description) {
        RoleResponse response = roleService.createRole(name, description);
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

}
