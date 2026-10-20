package com.ocp.organisation_service.controllers;


import com.ocp.organisation_service.dto.request.UserCreateRequestDTO;
import com.ocp.organisation_service.dto.request.UserUpdateRequestDTO;
import com.ocp.organisation_service.dto.response.UserResponseDTO;
import com.ocp.organisation_service.security.AuthenticatedUser;
import com.ocp.organisation_service.services.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserProfileController {
	private final UserProfileService service;

	@PostMapping
	public ResponseEntity<UserResponseDTO> create(@jakarta.validation.Valid @RequestBody UserCreateRequestDTO request) {
		UserResponseDTO response = service.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		return ResponseEntity.ok(service.getAllUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
		return ResponseEntity.ok(service.getUserById(id));
	}

	@GetMapping("/auth/{authUserId}")
	public ResponseEntity<UserResponseDTO> getUserByAuthUserId(
		@PathVariable UUID authUserId,
		@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
		if (authenticatedUser != null
			&& !authUserId.equals(authenticatedUser.userId())
			&& !authenticatedUser.hasAdministratorRole()) {
			throw new AccessDeniedException("Users can only read their own profile");
		}
		return ResponseEntity.ok(service.getUserByAuthUserId(authUserId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(
		@PathVariable UUID id,
		@jakarta.validation.Valid @RequestBody UserUpdateRequestDTO request) {
		return ResponseEntity.ok(service.updateUser(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
		service.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}


