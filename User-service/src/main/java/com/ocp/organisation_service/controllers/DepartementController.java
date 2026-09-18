package com.ocp.organisation_service.controllers;

import com.ocp.organisation_service.dto.request.DepartementRequestDTO;
import com.ocp.organisation_service.dto.response.DepartementResponseDTO;
import com.ocp.organisation_service.services.DepartementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/departements")
@RequiredArgsConstructor
public class DepartementController {

	private final DepartementService departementService;

	@GetMapping
	public ResponseEntity<List<DepartementResponseDTO>> getAllDepartements() {
		return ResponseEntity.ok(departementService.getAllDepartement());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DepartementResponseDTO> getDepartmentById(@PathVariable UUID id) {
		return ResponseEntity.ok(departementService.getDepartementById(id));
	}

	@PostMapping
	public ResponseEntity<DepartementResponseDTO> createDepartment(@RequestBody DepartementRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(departementService.createDepartement(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DepartementResponseDTO> updateDepartment(@PathVariable UUID id, @RequestBody DepartementRequestDTO request) {
		return ResponseEntity.ok(departementService.updateDepartement(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
		departementService.deleteDepartement(id);
		return ResponseEntity.noContent().build();
	}
}
