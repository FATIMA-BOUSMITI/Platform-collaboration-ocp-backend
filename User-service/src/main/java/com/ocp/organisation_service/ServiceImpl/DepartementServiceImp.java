package com.ocp.organisation_service.ServiceImpl;

import com.ocp.organisation_service.dto.request.DepartementRequestDTO;
import com.ocp.organisation_service.dto.response.DepartementResponseDTO;
import com.ocp.organisation_service.entity.Departement;
import com.ocp.organisation_service.mappers.DepartementMapper;
import com.ocp.organisation_service.repository.DepartementRepository;
import com.ocp.organisation_service.services.DepartementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DepartementServiceImp implements DepartementService {
	private final DepartementRepository departementRepository;
	private final DepartementMapper mapper;
 private final com.ocp.organisation_service.repository.UserProfileRepository users;

	@Override
	@Transactional(readOnly = true)
	public List<DepartementResponseDTO> getAllDepartement() {
		return departementRepository.findAll()
			.stream()
			.map(mapper::toResponse)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public DepartementResponseDTO getDepartementById(UUID id) {
		Departement departement = departementRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
		return mapper.toResponse(departement);
	}

	@Override
	@Transactional
	public DepartementResponseDTO createDepartement(DepartementRequestDTO request) {
		validateManager(request);
 Departement departement = mapper.toEntity(request);
		Departement saved = departementRepository.save(departement);
		return mapper.toResponse(saved);
	}

	@Override
	@Transactional
	public DepartementResponseDTO updateDepartement(UUID id, DepartementRequestDTO request) {
		Departement departement = departementRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

		validateManager(request);
        if (request.getName() != null) departement.setName(request.getName());
		if (request.getCode() != null) departement.setCode(request.getCode());
		if (request.getDescription() != null) departement.setDescription(request.getDescription());
		if (request.getManagerId() != null) departement.setManagerId(request.getManagerId());
		if (request.getStatus() != null) departement.setStatus(request.getStatus());

		return mapper.toResponse(departementRepository.save(departement));
	}

	@Override
	@Transactional
	public void deleteDepartement(UUID id) {
		Departement departement = departementRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
		if(!departement.getUsers().isEmpty() || !departement.getTeams().isEmpty())throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Réaffectez les utilisateurs et les équipes avant de supprimer ce département.");
 departementRepository.delete(departement);
	}
 private void validateManager(DepartementRequestDTO r){if(r.getManagerId()!=null && !users.existsById(r.getManagerId()))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Responsable introuvable");}
}
