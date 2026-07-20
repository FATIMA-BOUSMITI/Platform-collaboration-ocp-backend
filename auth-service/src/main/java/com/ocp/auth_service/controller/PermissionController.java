package com.ocp.auth_service.controller;
import com.ocp.auth_service.dto.request.CreatePermissionRequest;
import com.ocp.auth_service.dto.response.PermissionResponse;
import com.ocp.auth_service.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(
            @Valid @RequestBody CreatePermissionRequest request) {
        PermissionResponse response = permissionService.createPermission(
                request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }
}
