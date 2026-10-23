package com.ocp.organisation_service.services;

import com.ocp.organisation_service.dto.request.DepartementRequestDTO;
import com.ocp.organisation_service.dto.response.DepartementResponseDTO;

import java.util.List;
import java.util.UUID;

public interface DepartementService {
	List<DepartementResponseDTO> getAllDepartement();

	DepartementResponseDTO getDepartementById(UUID id);

	DepartementResponseDTO createDepartement(DepartementRequestDTO request);

	DepartementResponseDTO updateDepartement(UUID id, DepartementRequestDTO request);

	void deleteDepartement(UUID id);
}
